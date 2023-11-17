package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret in website. */
@Component
public class Challenge31 implements Challenge {

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

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(getanswer());
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return getanswer().equals(answer);
  }
}
