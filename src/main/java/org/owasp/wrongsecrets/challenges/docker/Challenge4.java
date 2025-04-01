package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about having a secrets stored as a Docker ARG var. */
@Component
public class Challenge4 extends FixedAnswerChallenge {

  private final String argBasedPassword;

  public Challenge4(@Value("${ARG_BASED_PASSWORD}") String argBasedPassword) {
    this.argBasedPassword = argBasedPassword;
  }

  @Override
  public String getAnswer() {
    return argBasedPassword;
  }
}
