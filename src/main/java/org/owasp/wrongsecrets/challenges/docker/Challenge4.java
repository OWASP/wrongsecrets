package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about having a secrets stored as a Docker ARG var. */
@Component
public class Challenge4 implements Challenge {

  private final String argBasedPassword;

  public Challenge4(@Value("${ARG_BASED_PASSWORD}") String argBasedPassword) {
    this.argBasedPassword = argBasedPassword;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(argBasedPassword);
  }

  @Override
  public boolean answerCorrect(String answer) {
    return argBasedPassword.equals(answer) || argBasedPassword.equals("'" + answer + "'");
  }
}
