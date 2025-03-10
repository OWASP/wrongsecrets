package org.owasp.wrongsecrets.challenges;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;

/** Abstract class for challenges with fixed answers, integrating binary execution. */
@Slf4j
public abstract class FixedAnswerChallenge implements Challenge {

  private final BinaryExecutionHelper binaryExecutionHelper;
  private final String executable;

  protected FixedAnswerChallenge(int challengeNumber) {
    this.executable = "wrongsecrets-challenge52-c-linux";
    this.binaryExecutionHelper = new BinaryExecutionHelper(challengeNumber, new MuslDetectorImpl());
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(binaryExecutionHelper.executeCommand("spoil", executable));
  }

  @Override
  public boolean answerCorrect(String answer) {
    return binaryExecutionHelper
        .executeCommand(answer, executable)
        .equals("This is correct! Congrats!");
  }
}
