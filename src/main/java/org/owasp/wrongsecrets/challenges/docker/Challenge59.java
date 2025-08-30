package org.owasp.wrongsecrets.challenges.docker;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret in a Telegram channel. */
@Component
public class Challenge59 implements Challenge {

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
    // The answer should be retrieved from the Telegram channel
    // but for this educational challenge, we'll hardcode it
    // In a real scenario, the secret would be posted in the channel
    // that can be accessed using the bot token below
    
    // Hardcoded bot token (intentionally vulnerable for educational purposes)
    String botToken = getBotToken();
    
    // For this challenge, the secret is what you would find in the Telegram channel
    // Bot: @WrongsecretsBot
    // The secret is: "telegram_secret_found_in_channel"
    return "telegram_secret_found_in_channel";
  }
  
  private String getBotToken() {
    // Double-encoded bot token to make it slightly more challenging
    // but still discoverable through code inspection
    return new String(
        Base64.decode(
            new String(
                Base64.decode("T0RFek1qZzJOalkwTXpwQlFVaEtiWFphY1haMlRUbGtTVEp5ZEVKUGRTMHRWMDFhZVUxR1ZHWklUbTg1U1E9PQo="), UTF_8)),
        UTF_8);
  }
}