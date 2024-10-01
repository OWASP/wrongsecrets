package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.EXECUTION_ERROR;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;
import org.springframework.stereotype.Component;

/**
 * This challenge is about finding a secret hardcoded in a dotnet binary. Given that the dotnetfiles
 * are very large, we need to get them with git LFS, hence if we cannot find the file we will log
 * that you need to do git lfs pull and show a spoiler issue when requested the config
 */
@Slf4j
@Component
public class Challenge50 implements Challenge {

  private final BinaryExecutionHelper binaryExecutionHelper;
  private static final String LFS_ERROR = "Please pull using GIT LFS";

  public Challenge50() {
    this.binaryExecutionHelper = new BinaryExecutionHelper(50, new MuslDetectorImpl());
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    final String answer = binaryExecutionHelper.executeCommand("", "wrongsecrets-dotnet");
    if (EXECUTION_ERROR.equals(answer)) {
      return new Spoiler(LFS_ERROR);
    }
    return new Spoiler(answer);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    final String actualAnswer = binaryExecutionHelper.executeCommand("", "wrongsecrets-dotnet");
    if (EXECUTION_ERROR.equals(actualAnswer)) {
      return LFS_ERROR.equals(answer);
    }
    return actualAnswer.equals(answer);
  }
}
