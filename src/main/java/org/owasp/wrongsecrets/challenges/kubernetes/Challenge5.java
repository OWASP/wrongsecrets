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

/** This challenge is about having a secrets stored as a K8s Configmap. */
@Component
@Order(5)
public class Challenge5 extends Challenge {

  private final String configmapK8sSecret;

  public Challenge5(
      ScoreCard scoreCard, @Value("${SPECIAL_K8S_SECRET}") String configmapK8sSecret) {
    super(scoreCard);
    this.configmapK8sSecret = configmapK8sSecret;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(configmapK8sSecret);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return configmapK8sSecret.equals(answer);
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

  /** {@inheritDoc} Configmaps based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.CONFIGMAPS.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return true;
  }
}
