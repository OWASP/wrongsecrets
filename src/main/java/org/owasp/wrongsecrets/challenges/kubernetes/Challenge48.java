package org.owasp.wrongsecrets.challenges.kubernetes;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Challenge48 extends FixedAnswerChallenge {

  private final String sealedSecret;

  /** This challenges is about how to misuse sealed secrets */
  public Challenge48(@Value("${SEALED_SECRET_ANSWER}") String sealedSecret) {
    this.sealedSecret = sealedSecret;
  }

  @Override
  public String getAnswer() {
    return this.sealedSecret;
  }
}
