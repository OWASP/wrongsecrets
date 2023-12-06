package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This challenge can be run in CTF mode and is limited to using Docker as a runtime environment.
 */
@Component
public class Challenge3 implements Challenge {

  private final String hardcodedEnvPassword;

  public Challenge3(@Value("${DOCKER_ENV_PASSWORD}") String hardcodedEnvPassword) {
    this.hardcodedEnvPassword = hardcodedEnvPassword;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(hardcodedEnvPassword);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return hardcodedEnvPassword.equals(answer);
  }
}
