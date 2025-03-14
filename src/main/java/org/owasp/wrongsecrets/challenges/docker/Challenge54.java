package org.owasp.wrongsecrets.challenges;

import org.springframework.stereotype.Component;

/** Challenge with a secret in .gitignore */
@Component
public class Challenge54 extends FixedAnswerChallenge {

  private final String secret = "(<:GITIGN0RE_SECRET:>)";

  @Override
  public String getAnswer() {
    return secret;
  }
}
