package org.owasp.wrongsecrets.challenges.docker;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class Challenge59Test {

  @Test
  void answerCorrectWithValidWebhookUrl() {
    // Create a properly obfuscated Slack webhook URL
    String originalUrl = "https://hooks.slack.com/services/T123456789/B123456789/1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p";
    String firstEncode = Base64.getEncoder().encodeToString(originalUrl.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertTrue(challenge.answerCorrect(originalUrl));
  }

  @Test
  void answerIncorrectWithWrongUrl() {
    String originalUrl = "https://hooks.slack.com/services/T123456789/B123456789/1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p";
    String firstEncode = Base64.getEncoder().encodeToString(originalUrl.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertFalse(challenge.answerCorrect("https://wrong-webhook-url.com"));
  }

  @Test
  void answerIncorrectWithEmptyString() {
    String originalUrl = "https://hooks.slack.com/services/T123456789/B123456789/1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p";
    String firstEncode = Base64.getEncoder().encodeToString(originalUrl.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertFalse(challenge.answerCorrect(""));
  }

  @Test
  void getSlackWebhookUrlReturnsDeobfuscatedUrl() {
    String originalUrl = "https://hooks.slack.com/services/T123456789/B123456789/1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p";
    String firstEncode = Base64.getEncoder().encodeToString(originalUrl.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertEquals(originalUrl, challenge.getSlackWebhookUrl());
  }

  @Test
  void handlesInvalidObfuscatedUrl() {
    // Test with invalid base64 input
    Challenge59 challenge = new Challenge59("invalid-base64-url");
    
    // Should return the default URL when deobfuscation fails
    String defaultUrl = "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX";
    assertEquals(defaultUrl, challenge.getAnswer());
  }

  @Test
  void answerCorrectWithDefaultUrl() {
    // Test with invalid input that falls back to default
    Challenge59 challenge = new Challenge59("invalid-input");
    String defaultUrl = "https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXXXXXX";
    assertTrue(challenge.answerCorrect(defaultUrl));
  }
}