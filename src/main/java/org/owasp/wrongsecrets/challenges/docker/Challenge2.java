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
 * This challenge requires the participant to provide a hardcoded password to pass the challenge.
 * This challenge can be run in CTF mode and is limited to using Docker as a runtime environment.
 * The javadoc is generated using ChatGPT.
 */
@Component
@Order(2)
public class Challenge2 extends Challenge {

  private final String hardcodedPassword;

  /**
   * Constructor for creating a new Challenge2 object.
   *
   * @param scoreCard The scorecard object used for tracking points.
   * @param hardcodedPassword The hardcoded password for the challenge.
   */
  public Challenge2(ScoreCard scoreCard, @Value("${password}") String hardcodedPassword) {
    super(scoreCard);
    this.hardcodedPassword = hardcodedPassword;
  }

  /** {@inheritDoc} This challenge can always run in CTF mode. */
  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(hardcodedPassword);
  }

  /**
   * {@inheritDoc} Checks if the provided answer matches the hardcoded password for the challenge.
   *
   * @param answer The answer provided by the participant.
   * @return True if the answer matches the hardcoded password, false otherwise.
   */
  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return hardcodedPassword.equals(answer);
  }

  /** {@inheritDoc} This challenge supports the Docker runtime environment. */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EASY;
  }

  /** {@inheritDoc} Git based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.GIT.id;
  }

  /** {@inheritDoc} This challenge is not limited when hosted online. */
  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }
}
