package org.owasp.wrongsecrets.challenges.docker;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

import java.util.List;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret in a Github issue. */
@Component
@Order(28)
public class Challenge28 extends Challenge {

  public Challenge28(ScoreCard scoreCard) {
    super(scoreCard);
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(getSecretKey());
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return getSecretKey().equals(answer);
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(DOCKER);
  }

  /** {@inheritDoc} This is an easy challenge */
  @Override
  public int difficulty() {
    return Difficulty.EASY;
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

  private String getSecretKey() {
    return new String(
        Base64.decode(
            new String(
                Base64.decode("WVhOa1ptUndkVmxWU1dGa1ltRnZZWE5rY0dFd04ydHFNakF3TXc9PQ=="), UTF_8)),
        UTF_8);
  }
}
