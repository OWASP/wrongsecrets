package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/**
 * This is a binary based challenge with encryption involved to make it harder to find the answer.
 */
@Slf4j
@Component
public class Challenge36 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    BinaryExecutionHelper binaryExecutionHelper =
        new BinaryExecutionHelper(36, new MuslDetectorImpl());
    return binaryExecutionHelper.executeCommand("spoil", "wrongsecrets-advanced-c");
  }
}
