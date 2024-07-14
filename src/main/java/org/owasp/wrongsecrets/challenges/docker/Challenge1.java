package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** Challenge to find the hardcoded password in code. */
@Component
public class Challenge1 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return WrongSecretsConstants.password;
  }
}
