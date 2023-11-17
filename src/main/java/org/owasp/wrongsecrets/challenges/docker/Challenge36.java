package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/**
 * This is a binary based challenge with encryption involved to make it harder to find the answer.
 */
@Slf4j
@Component
public class Challenge36 implements Challenge {
  private final BinaryExecutionHelper binaryExecutionHelper;
  private String executable;

  public Challenge36() {
    this.executable = "wrongsecrets-advanced-c";
    this.binaryExecutionHelper = new BinaryExecutionHelper(36, new MuslDetectorImpl());
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
