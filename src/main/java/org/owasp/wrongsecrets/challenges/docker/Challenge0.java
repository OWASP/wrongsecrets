package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** Introduction challenge to get a user introduced with the setup. */
@Component
public class Challenge0 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return "The first answer";
  }
}
