package org.owasp.wrongsecrets.challenges.docker;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This challenge demonstrates the security risk of hardcoded Slack webhook URLs in environment
 * variables. Shows how an ex-employee could misuse the webhook if it's not rotated when they leave.
 */
@Component
public class Challenge59 extends FixedAnswerChallenge {

  private final String obfuscatedSlackWebhookUrl;

  public Challenge59(@Value("${CHALLENGE59_SLACK_WEBHOOK_URL}") String obfuscatedSlackWebhookUrl) {
    this.obfuscatedSlackWebhookUrl = obfuscatedSlackWebhookUrl;
  }

  @Override
  public String getAnswer() {
    return deobfuscateSlackWebhookUrl(obfuscatedSlackWebhookUrl);
  }

  /**
   * Deobfuscates the Slack webhook URL. The URL is base64 encoded twice to avoid detection by
   * security scanners.
   */
  private String deobfuscateSlackWebhookUrl(String obfuscatedUrl) {
    try {
      // First decode from base64
      byte[] firstDecode = Base64.getDecoder().decode(obfuscatedUrl);
      // Second decode from base64
      byte[] secondDecode = Base64.getDecoder().decode(firstDecode);
      return new String(secondDecode, UTF_8);
    } catch (Exception e) {
      // Return a default value if the environment variable is not properly set
      return "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX";
    }
  }

  /**
   * Gets the deobfuscated Slack webhook URL for use in Slack notifications. This method is used by
   * the Slack integration service.
   */
  public String getSlackWebhookUrl() {
    return getAnswer();
  }
}
