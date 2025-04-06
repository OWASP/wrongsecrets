package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/** challenge about dotnet binaries. Please download them from the wrongsecrets-binaries release. */
@Slf4j
@Component
public class Challenge50 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    BinaryExecutionHelper binaryExecutionHelper =
        new BinaryExecutionHelper(50, new MuslDetectorImpl());
    return binaryExecutionHelper.executeCommand("", "wrongsecrets-dotnet");
  }
}
