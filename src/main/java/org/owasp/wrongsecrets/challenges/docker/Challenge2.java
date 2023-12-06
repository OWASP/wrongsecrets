package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This challenge requires the participant to provide a hardcoded password to pass the challenge.
 * This challenge can be run in CTF mode and is limited to using Docker as a runtime environment.
 * The javadoc is generated using ChatGPT.
 */
@Component
public class Challenge2 implements Challenge {

  private final String hardcodedPassword;

  /**
   * Constructor for creating a new Challenge2 object.
   *
   * @param hardcodedPassword The hardcoded password for the challenge.
   */
  public Challenge2(@Value("${password}") String hardcodedPassword) {
    this.hardcodedPassword = hardcodedPassword;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(hardcodedPassword);
  }

  /**
   * {@inheritDoc} Checks if the provided answer matches the hardcoded password for the challenge.
   *
   * @param answer The answer provided by the participant.
   * @return True if the answer matches the hardcoded password, false otherwise.
   */
  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return hardcodedPassword.equals(answer);
  }
}
