package org.owasp.wrongsecrets.challenges.kubernetes;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about having a secrets stored as a K8s Secret. */
@Component
public class Challenge6 extends FixedAnswerChallenge {

  private final String secretK8sSecret;

  public Challenge6(@Value("${SPECIAL_SPECIAL_K8S_SECRET}") String secretK8sSecret) {
    this.secretK8sSecret = secretK8sSecret;
  }

  @Override
  public String getAnswer() {
    return secretK8sSecret;
  }
}
