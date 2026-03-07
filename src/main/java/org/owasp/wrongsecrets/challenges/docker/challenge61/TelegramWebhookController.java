package org.owasp.wrongsecrets.challenges.docker.challenge61;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Duration;
import java.util.Map;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Optional webhook controller for Challenge61. Enable by setting challenge61.webhook.enabled=true
 * and challenge61.webhook.token=<your-secret-token> This is a better approach for production than
 * polling with getUpdates.
 *
 * <p>To use: 1. Set environment variables: - CHALLENGE61_WEBHOOK_ENABLED=true -
 * CHALLENGE61_WEBHOOK_TOKEN=<random-secret-string> 2. Set webhook URL with Telegram: curl -X POST
 * "https://api.telegram.org/bot<BOT_TOKEN>/setWebhook?url=https://<your-heroku-app>.herokuapp.com/telegram/webhook/challenge61&secret_token=<your-secret-token>"
 */
@RestController
@ConditionalOnProperty(name = "challenge61.webhook.enabled", havingValue = "true")
public class TelegramWebhookController {

  private static final Logger logger = LoggerFactory.getLogger(TelegramWebhookController.class);
  private final RestTemplate restTemplate;
  private final String webhookToken;

  public TelegramWebhookController(@Value("${challenge61.webhook.token:}") String webhookToken) {
    var requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(Duration.ofSeconds(5));
    requestFactory.setReadTimeout(Duration.ofSeconds(5));
    this.restTemplate = new RestTemplate(requestFactory);
    this.webhookToken = webhookToken;
    logger.info("Challenge61 Telegram webhook controller enabled");
  }

  @PostMapping("/telegram/webhook/challenge61")
  public ResponseEntity<String> handleWebhook(
      @RequestBody Map<String, Object> update,
      @org.springframework.web.bind.annotation.RequestHeader(
              value = "X-Telegram-Bot-Api-Secret-Token",
              required = false)
          String secretToken) {

    // Verify the secret token to ensure the request is from Telegram
    if (!webhookToken.isEmpty() && !webhookToken.equals(secretToken)) {
      logger.warn("Invalid webhook secret token received");
      return ResponseEntity.status(403).body("Forbidden");
    }

    try {
      logger.info("Received webhook update: {}", sanitizeForLog(String.valueOf(update.get("update_id"))));

      // Check if this is a message update
      if (update.containsKey("message")) {
        var message = (Map<String, Object>) update.get("message");
        var text = (String) message.get("text");

        // Respond to /start command
        if ("/start".equals(text)) {
          var chat = (Map<String, Object>) message.get("chat");
          Object chatId = chat != null ? chat.get("id") : null;

          if (chatId != null) {
            sendSecretMessage(chatId);
          }
        }
      }

      return ResponseEntity.ok("OK");

    } catch (Exception e) {
      logger.error("Error processing webhook update", e);
      return ResponseEntity.status(500).body("Error");
    }
  }

  private void sendSecretMessage(Object chatId) {
    try {
      // Base64 encoded start message
      String encodedMessage =
          "V2VsY29tZSEgWW91ciBzZWNyZXQgaXM6IHRlbGVncmFtX3NlY3JldF9mb3VuZF9pbl9jaGFubmVs";
      String decodedMessage = new String(Base64.decode(encodedMessage), UTF_8);

      // Get bot token (same as in Challenge61)
      String botToken = getBotToken();

      String sendMessageUrl =
          "https://api.telegram.org/bot"
              + botToken
              + "/sendMessage?chat_id="
              + chatId
              + "&text="
              + java.net.URLEncoder.encode(decodedMessage, UTF_8);

      Map<String, Object> response = restTemplate.getForObject(sendMessageUrl, Map.class);

      if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
        logger.info("Successfully sent secret message to chat_id: {}", sanitizeForLog(String.valueOf(chatId)));
      } else {
        logger.warn("Failed to send message to Telegram");
      }

    } catch (Exception e) {
      logger.error("Error sending secret message", e);
    }
  }

  private String sanitizeForLog(String value) {
    if (value == null) {
      return "null";
    }
    return value.replaceAll("[\r\n]", "_");
  }

  private String getBotToken() {
    // Same double-encoded bot token as in Challenge61
    String encodedToken =
        "T0RFek1qZzJOalkwTXpwQlFVaEtiWFphY1haMlRUbGtTVEp5ZEVKUGRTMHRWMDFhZVUxR1ZHWklUbTg1U1E9PQo=";
    String firstDecode = new String(Base64.decode(encodedToken), UTF_8);
    return new String(Base64.decode(firstDecode), UTF_8);
  }
}
