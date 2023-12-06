package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge31Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge31();

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler(getanswer()));
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge31();
    assertEquals(challenge.answerCorrect("wrong answer"), false);

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }

  private String getanswer() {
    String str = "vozvtbeY6++kjJz3tPn84LeM77I=";
    byte[] arr = Base64.getDecoder().decode(str);

    byte[] invertedBytes = new byte[arr.length];
    for (int i = 0; i < arr.length; i++) {
      invertedBytes[i] = (byte) (~arr[i] & 0xff);
    }

    UUID uuid = UUID.fromString("12345678-1234-5678-1234-567812345678");
    byte[] uuidBytes = new byte[16];
    long msb = uuid.getMostSignificantBits();
    long lsb = uuid.getLeastSignificantBits();
    for (int i = 0; i < 8; i++) {
      uuidBytes[i] = (byte) ((msb >>> (8 * (7 - i))) & 0xff);
      uuidBytes[8 + i] = (byte) ((lsb >>> (8 * (7 - i))) & 0xff);
    }

    byte[] xoredBytes = new byte[invertedBytes.length];
    for (int i = 0; i < invertedBytes.length; i++) {
      xoredBytes[i] = (byte) (invertedBytes[i] ^ uuidBytes[i % uuidBytes.length]);
    }

    return new String(xoredBytes, StandardCharsets.UTF_8);
  }
}
