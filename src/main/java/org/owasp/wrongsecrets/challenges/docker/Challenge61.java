package org.owasp.wrongsecrets.challenges.docker;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Duration;
import java.util.Map;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/** This challenge is about finding a secret in a Telegram channel. */
@Component
public class Challenge61 implements Challenge {

  private static final Logger logger = LoggerFactory.getLogger(Challenge61.class);
  private final RestTemplate restTemplate;

  public Challenge61() {
    var requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(Duration.ofSeconds(5));
    requestFactory.setReadTimeout(Duration.ofSeconds(5));
    this.restTemplate = new RestTemplate(requestFactory);
  }

  // Constructor for testing with mocked RestTemplate
  Challenge61(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(getTelegramSecret());
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return getTelegramSecret().equals(answer);
  }

  private String getTelegramSecret() {
    // First try to get the secret from the Telegram channel using the bot token
    String botToken = getBotToken();
    String secretFromChannel = getSecretFromTelegramChannel(botToken);

    if (secretFromChannel != null) {
      return secretFromChannel;
    }

    // Fallback to hardcoded answer if API call fails
    // This ensures the challenge works even if the bot token is invalid
    // or if there are network connectivity issues
    logger.warn("Failed to retrieve secret from Telegram channel, using fallback answer");
    return "telegram_secret_found_in_channel";
  }

  /**
   * Attempts to retrieve secret from Telegram channel using the embedded bot token. This
   * demonstrates how hardcoded credentials can be used to access external services.
   *
   * @param botToken The Telegram bot token extracted from the code
   * @return The secret if found, null if API call fails
   */
  private String getSecretFromTelegramChannel(String botToken) {
    try {
      logger.info(
          "Attempting to call Telegram Bot API with token: {}...",
          botToken.substring(0, Math.min(10, botToken.length())));

      // Call Telegram Bot API to get bot info first (simpler call)
      String url = "https://api.telegram.org/bot" + botToken + "/getMe";
      Map<String, Object> response = restTemplate.getForObject(url, Map.class);

      if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
        logger.info("Successfully authenticated with Telegram Bot API");

        // Send start message with encoded secret
        sendStartMessage(botToken);

        // In a real scenario, we would call getUpdates or similar to get channel messages
        // For this educational challenge, we simulate finding the secret
        // after successfully authenticating with the API
        return "telegram_secret_found_in_channel";
      }

    } catch (RestClientException e) {
      logger.warn("Telegram API call failed: {}", e.getMessage());
    } catch (Exception e) {
      logger.warn("Failed to call Telegram API: {}", e.getMessage());
    }

    return null;
  }

  /**
   * Sends a start message containing the secret to the bot. The message is base64 encoded in the
   * challenge code but sent decoded via the Telegram API. This method checks for incoming /start
   * commands and responds to them. Uses timeout=0 and limit=1 to minimize conflicts between
   * multiple app instances.
   *
   * @param botToken The Telegram bot token
   */
  private void sendStartMessage(String botToken) {
    try {
      // Base64 encoded start message: "Welcome! Your secret is: telegram_secret_found_in_channel"
      String encodedMessage =
          "V2VsY29tZSEgWW91ciBzZWNyZXQgaXM6IHRlbGVncmFtX3NlY3JldF9mb3VuZF9pbl9jaGFubmVs";
      String decodedMessage = new String(Base64.decode(encodedMessage), UTF_8);

      logger.info("Checking for new messages and sending start message with decoded secret");

      // Get updates with timeout=0 (no long polling) and limit=1 to get just one update
      // This minimizes conflicts when multiple app instances are running
      String updatesUrl =
          "https://api.telegram.org/bot" + botToken + "/getUpdates?timeout=0&limit=1";
      Map<String, Object> updatesResponse = restTemplate.getForObject(updatesUrl, Map.class);

      if (updatesResponse != null
          && Boolean.TRUE.equals(updatesResponse.get("ok"))
          && updatesResponse.containsKey("result")) {

        var results = (java.util.List<?>) updatesResponse.get("result");
        if (results != null && !results.isEmpty()) {
          // Process each update and respond
          for (var update : results) {
            var updateMap = (Map<String, Object>) update;
            var message = (Map<String, Object>) updateMap.get("message");

            if (message != null) {
              var chat = (Map<String, Object>) message.get("chat");
              Object chatId = chat != null ? chat.get("id") : null;

              if (chatId != null) {
                // Send the decoded message to the user
                String sendMessageUrl =
                    "https://api.telegram.org/bot"
                        + botToken
                        + "/sendMessage?chat_id="
                        + chatId
                        + "&text="
                        + java.net.URLEncoder.encode(decodedMessage, UTF_8);

                Map<String, Object> sendResponse =
                    restTemplate.getForObject(sendMessageUrl, Map.class);

                if (sendResponse != null && Boolean.TRUE.equals(sendResponse.get("ok"))) {
                  logger.info("Successfully sent start message to chat_id: {}", chatId);

                  // Mark this update as processed by acknowledging it with offset
                  // This prevents the same update from being processed multiple times
                  Object updateId = updateMap.get("update_id");
                  if (updateId != null) {
                    String ackUrl =
                        "https://api.telegram.org/bot"
                            + botToken
                            + "/getUpdates?offset="
                            + ((Number) updateId).longValue()
                            + 1;
                    restTemplate.getForObject(ackUrl, Map.class);
                    logger.debug("Acknowledged update_id: {}", updateId);
                  }
                } else {
                  logger.warn("Failed to send message to Telegram");
                }
              }
            }
          }
        } else {
          logger.debug("No messages found, message will be sent when user starts bot");
        }
      }

    } catch (RestClientException e) {
      logger.warn("Failed to send start message via Telegram API: {}", e.getMessage());
    } catch (Exception e) {
      logger.warn("Failed to send start message: {}", e.getMessage());
    }
  }

  private String getBotToken() {
    // Double-encoded bot token to make it slightly more challenging
    // but still discoverable through code inspection
    String encodedToken =
        "T0RFek1qZzJOalkwTXpwQlFVaEtiWFphY1haMlRUbGtTVEp5ZEVKUGRTMHRWMDFhZVUxR1ZHWklUbTg1U1E9PQo=";
    String firstDecode = new String(Base64.decode(encodedToken), UTF_8);
    return new String(Base64.decode(firstDecode), UTF_8);
  }
}
