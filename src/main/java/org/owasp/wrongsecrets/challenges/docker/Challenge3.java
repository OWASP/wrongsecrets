package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

import java.util.List;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This challenge can be run in CTF mode and is limited to using Docker as a runtime environment.
 */
@Component
@Order(3)
public class Challenge3 extends Challenge {

  private final String hardcodedEnvPassword;

  public Challenge3(
      ScoreCard scoreCard, @Value("${DOCKER_ENV_PASSWORD}") String hardcodedEnvPassword) {
    super(scoreCard);
    this.hardcodedEnvPassword = hardcodedEnvPassword;
  }

  /** {@inheritDoc} */
  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(hardcodedEnvPassword);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return hardcodedEnvPassword.equals(answer);
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EASY;
  }

  /** {@inheritDoc} Docker based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.DOCKER.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }
}
