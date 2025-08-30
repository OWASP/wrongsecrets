package org.owasp.wrongsecrets.challenges.docker;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.Duration;
import java.util.Map;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/** This challenge is about finding a secret in a Telegram channel. */
@Component
public class Challenge59 implements Challenge {

  private static final Logger logger = LoggerFactory.getLogger(Challenge59.class);
  private final RestTemplate restTemplate;

  public Challenge59() {
    this.restTemplate = new RestTemplateBuilder()
        .rootUri("https://api.telegram.org")
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(5))
        .build();
  }

  // Constructor for testing with mocked RestTemplate
  Challenge59(RestTemplate restTemplate) {
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
   * Attempts to retrieve secret from Telegram channel using the embedded bot token.
   * This demonstrates how hardcoded credentials can be used to access external services.
   * 
   * @param botToken The Telegram bot token extracted from the code
   * @return The secret if found, null if API call fails
   */
  private String getSecretFromTelegramChannel(String botToken) {
    try {
      logger.info("Attempting to call Telegram Bot API with token: {}...", 
          botToken.substring(0, Math.min(10, botToken.length())));
      
      // Call Telegram Bot API to get bot info first (simpler call)
      String url = "/bot" + botToken + "/getMe";
      Map<String, Object> response = restTemplate.getForObject(url, Map.class);
      
      if (response != null && Boolean.TRUE.equals(response.get("ok"))) {
        logger.info("Successfully authenticated with Telegram Bot API");
        
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
  
  private String getBotToken() {
    // Double-encoded bot token to make it slightly more challenging
    // but still discoverable through code inspection
    String encodedToken = "T0RFek1qZzJOalkwTXpwQlFVaEtiWFphY1haMlRUbGtTVEp5ZEVKUGRTMHRWMDFhZVUxR1ZHWklUbTg1U1E9PQo=";
    String firstDecode = new String(Base64.decode(encodedToken), UTF_8);
    return new String(Base64.decode(firstDecode), UTF_8);
  }
}