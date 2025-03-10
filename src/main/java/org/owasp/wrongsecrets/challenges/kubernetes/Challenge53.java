package org.owasp.wrongsecrets.challenges.kubernetes;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/** Challenge53 with a focus on sidecars */
@Component
public class Challenge53 extends FixedAnswerChallenge {

  private final BinaryExecutionHelper binaryExecutionHelper;
  private final String executable;

  protected Challenge53() {
    this.executable = "wrongsecrets-challenge53-c";
    this.binaryExecutionHelper = new BinaryExecutionHelper(53, new MuslDetectorImpl());
  }

  @Override
  public String getAnswer() {
    return binaryExecutionHelper.executeCommand("", executable);
  }
}
