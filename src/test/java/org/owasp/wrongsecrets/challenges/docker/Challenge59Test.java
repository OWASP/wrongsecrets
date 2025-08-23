package org.owasp.wrongsecrets.challenges.docker;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class Challenge59Test {

  @Test
  void answerCorrectWithValidKey() {
    // Create a properly obfuscated Slack key
    String originalKey = "xoxb-1234567890-1234567890-abcdefghijklmnopqrstuvwx";
    String firstEncode = Base64.getEncoder().encodeToString(originalKey.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertTrue(challenge.answerCorrect(originalKey));
  }

  @Test
  void answerIncorrectWithWrongKey() {
    String originalKey = "xoxb-1234567890-1234567890-abcdefghijklmnopqrstuvwx";
    String firstEncode = Base64.getEncoder().encodeToString(originalKey.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertFalse(challenge.answerCorrect("wrong-slack-key"));
  }

  @Test
  void answerIncorrectWithEmptyString() {
    String originalKey = "xoxb-1234567890-1234567890-abcdefghijklmnopqrstuvwx";
    String firstEncode = Base64.getEncoder().encodeToString(originalKey.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertFalse(challenge.answerCorrect(""));
  }

  @Test
  void getSlackKeyReturnsDeobfuscatedKey() {
    String originalKey = "xoxb-1234567890-1234567890-abcdefghijklmnopqrstuvwx";
    String firstEncode = Base64.getEncoder().encodeToString(originalKey.getBytes());
    String doubleEncoded = Base64.getEncoder().encodeToString(firstEncode.getBytes());

    Challenge59 challenge = new Challenge59(doubleEncoded);
    assertEquals(originalKey, challenge.getSlackKey());
  }

  @Test
  void handlesInvalidObfuscatedKey() {
    // Test with invalid base64 input
    Challenge59 challenge = new Challenge59("invalid-base64-key");
    
    // Should return the default key when deobfuscation fails
    String defaultKey = "xoxb-1234567890-1234567890-abcdefghijklmnopqrstuvwx";
    assertEquals(defaultKey, challenge.getAnswer());
  }

  @Test
  void answerCorrectWithDefaultKey() {
    // Test with invalid input that falls back to default
    Challenge59 challenge = new Challenge59("invalid-input");
    String defaultKey = "xoxb-1234567890-1234567890-abcdefghijklmnopqrstuvwx";
    assertTrue(challenge.answerCorrect(defaultKey));
  }
}