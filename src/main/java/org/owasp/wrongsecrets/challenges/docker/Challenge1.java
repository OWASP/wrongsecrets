package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/** Challenge to find the hardcoded password in code. */
@Component
public class Challenge1 implements Challenge {

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(WrongSecretsConstants.password);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return WrongSecretsConstants.password.equals(answer);
  }
}
