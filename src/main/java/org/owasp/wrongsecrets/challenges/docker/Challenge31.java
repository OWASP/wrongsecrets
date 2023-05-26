package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret in website */
@Component
@Order(31)
public class Challenge31 extends Challenge {

  public Challenge31(ScoreCard scoreCard) {
    super(scoreCard);
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

  @Override
  public boolean canRunInCTFMode() {
    return true;
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

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return 1;
  }

  /** {@inheritDoc} Documentation based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.DOCUMENTATION.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }
}
