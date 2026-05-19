package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in a Swift binary. */
@Slf4j
@Component
public class Challenge64 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    BinaryExecutionHelper binaryExecutionHelper =
        new BinaryExecutionHelper(64, new MuslDetectorImpl());
    return binaryExecutionHelper.executeCommand("", "wrongsecrets-swift");
  }
}
