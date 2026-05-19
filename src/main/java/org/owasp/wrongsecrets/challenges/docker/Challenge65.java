package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in a plain Java CLI JAR. */
@Component
public class Challenge65 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    BinaryExecutionHelper binaryExecutionHelper =
        new BinaryExecutionHelper(65, new MuslDetectorImpl());
    return binaryExecutionHelper.executeJavaJar("", "wrongsecrets-java.jar");
  }
}
