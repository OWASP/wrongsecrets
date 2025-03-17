package org.owasp.wrongsecrets.challenges;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** Challenge with a secret in .ssh */
@Component
public class Challenge55 extends FixedAnswerChallenge {

  private final String secret = "(<:SSH_SECRET:>)";

  @Override
  public String getAnswer() {
    return secret;
  }
}
