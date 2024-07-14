package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in a C++ binary. */
@Slf4j
@Component
public class Challenge20 implements Challenge {

  private final BinaryExecutionHelper binaryExecutionHelper;

  public Challenge20() {
    this.binaryExecutionHelper = new BinaryExecutionHelper(20, new MuslDetectorImpl());
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(binaryExecutionHelper.executeCommand("", "wrongsecrets-cplus"));
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return binaryExecutionHelper
        .executeCommand(answer, "wrongsecrets-cplus")
        .equals("This is correct! Congrats!");
  }
}
