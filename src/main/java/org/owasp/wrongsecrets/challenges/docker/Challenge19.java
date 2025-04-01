package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in a C binary. */
@Slf4j
@Component
public class Challenge19 extends FixedAnswerChallenge {

  private final BinaryExecutionHelper binaryExecutionHelper;

  public Challenge19() {
    this.binaryExecutionHelper = new BinaryExecutionHelper(19, new MuslDetectorImpl());
  }

  @Override
  public String getAnswer() {
    return binaryExecutionHelper.executeCommand("", "wrongsecrets-c");
  }
}
