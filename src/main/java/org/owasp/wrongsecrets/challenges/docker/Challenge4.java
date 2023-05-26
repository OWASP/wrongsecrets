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

/** This challenge is about having a secrets stored as a Docker ARG var. */
@Component
@Order(4)
public class Challenge4 extends Challenge {

  private final String argBasedPassword;

  public Challenge4(ScoreCard scoreCard, @Value("${ARG_BASED_PASSWORD}") String argBasedPassword) {
    super(scoreCard);
    this.argBasedPassword = argBasedPassword;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(argBasedPassword);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return argBasedPassword.equals(answer) || argBasedPassword.equals("'" + answer + "'");
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.NORMAL;
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
