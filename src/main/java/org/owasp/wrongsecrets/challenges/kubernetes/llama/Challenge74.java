package org.owasp.wrongsecrets.challenges.kubernetes.llama;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

@Component
public class Challenge74 extends FixedAnswerChallenge {

  private static final String SECRET = "WRONGSECRETSISAWESOME";

  @Override
  public String getAnswer() {
    return SECRET;
  }
}
