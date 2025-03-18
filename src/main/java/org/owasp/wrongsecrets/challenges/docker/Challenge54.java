package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** Challenge with a secret in .gitignore */
@Component
public class Challenge54 extends FixedAnswerChallenge {

  private static final String secret = "(<:GITIGN0RE_SECRET:>)";

  @Override
  public String getAnswer() {
    return secret;
  }
}
