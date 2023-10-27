package org.owasp.wrongsecrets.challenges.kubernetes;

import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about having a secrets stored as a K8s Configmap. */
@Component
public class Challenge5 implements Challenge {

  private final String configmapK8sSecret;

  public Challenge5(@Value("${SPECIAL_K8S_SECRET}") String configmapK8sSecret) {
    this.configmapK8sSecret = configmapK8sSecret;
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
}
