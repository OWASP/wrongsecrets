package org.owasp.wrongsecrets.challenges.kubernetes;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;

public class Challenge47 extends FixedAnswerChallenge {

  private final String secret;

  public Challenge47(@Value("${challenge47secret}") String secret) {
    this.secret = secret;
  }

  @Override
  public String getAnswer() {
    return secret;
  }
}
