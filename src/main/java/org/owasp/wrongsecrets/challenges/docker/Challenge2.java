package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This challenge requires the participant to provide a hardcoded password to pass the challenge.
 * This challenge can be run in CTF mode and is limited to using Docker as a runtime environment.
 * The javadoc is generated using ChatGPT.
 */
@Component
public class Challenge2 extends FixedAnswerChallenge {

  private final String hardcodedPassword;

  /**
   * Constructor for creating a new Challenge2 object.
   *
   * @param hardcodedPassword The hardcoded password for the challenge.
   */
  public Challenge2(@Value("${password}") String hardcodedPassword) {
    this.hardcodedPassword = hardcodedPassword;
  }

  @Override
  public String getAnswer() {
    return this.hardcodedPassword;
  }
}
