package org.owasp.wrongsecrets.challenges.kubernetes;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Challenge47 extends FixedAnswerChallenge {

  private final String secret;

  /** This challenge is about having a secrets injected via Vault template. */
  public Challenge47(@Value("${challenge47secret}") String secret) {
    this.secret = secret;
  }

  @Override
  public String getAnswer() {
    return secret;
  }
}
