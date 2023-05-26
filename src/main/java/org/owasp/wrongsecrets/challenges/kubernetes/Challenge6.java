package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.K8S;

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

/** This challenge is about having a secrets stored as a K8s Secret. */
@Component
@Order(6)
public class Challenge6 extends Challenge {

  private final String secretK8sSecret;

  public Challenge6(
      ScoreCard scoreCard, @Value("${SPECIAL_SPECIAL_K8S_SECRET}") String secretK8sSecret) {
    super(scoreCard);
    this.secretK8sSecret = secretK8sSecret;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(secretK8sSecret);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return secretK8sSecret.equals(answer);
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(K8S);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.NORMAL;
  }

  /** {@inheritDoc} K8s secrets based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.SECRETS.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return true;
  }
}
