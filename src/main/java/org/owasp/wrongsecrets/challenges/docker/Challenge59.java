package org.owasp.wrongsecrets.challenges.docker;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This challenge demonstrates the security risk of hardcoded Slack API keys in environment
 * variables. Shows how an ex-employee could misuse the key if it's not rotated when they leave.
 */
@Component
public class Challenge59 extends FixedAnswerChallenge {

  private final String obfuscatedSlackKey;

  public Challenge59(@Value("${CHALLENGE59_SLACK_TOKEN}") String obfuscatedSlackKey) {
    this.obfuscatedSlackKey = obfuscatedSlackKey;
  }

  @Override
  public String getAnswer() {
    return deobfuscateSlackKey(obfuscatedSlackKey);
  }

  /**
   * Deobfuscates the Slack API key. The key is base64 encoded twice to avoid detection by Slack's
   * secret scanning.
   */
  private String deobfuscateSlackKey(String obfuscatedKey) {
    try {
      // First decode from base64
      byte[] firstDecode = Base64.getDecoder().decode(obfuscatedKey);
      // Second decode from base64
      byte[] secondDecode = Base64.getDecoder().decode(firstDecode);
      return new String(secondDecode, UTF_8);
    } catch (Exception e) {
      // Return a default value if the environment variable is not properly set
      return "xoxb-1234567890-1234567890-abcdefghijklmnopqrstuvwx";
    }
  }

  /**
   * Gets the deobfuscated Slack key for use in Slack notifications. This method is used by the
   * Slack integration service.
   */
  public String getSlackKey() {
    return getAnswer();
  }
}