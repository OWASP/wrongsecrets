package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in a Rust binary. */
@Slf4j
@Component
public class Challenge22 implements Challenge {

  private final BinaryExecutionHelper binaryExecutionHelper;

  public Challenge22() {
    this.binaryExecutionHelper = new BinaryExecutionHelper(22, new MuslDetectorImpl());
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(binaryExecutionHelper.executeCommand("", "wrongsecrets-rust"));
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return binaryExecutionHelper
        .executeCommand(answer, "wrongsecrets-rust")
        .equals("This is correct! Congrats!");
  }
}
