package org.owasp.wrongsecrets.challenges.docker;

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
    try {
      return new Spoiler(binaryExecutionHelper.executeCommand("", "wrongsecrets-dotnet"));
    } catch (Exception e) {
      log.error("Error with executing the spoil command, did you run LFS?", e);
      return new Spoiler(LFS_ERROR);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    try {
      return binaryExecutionHelper
          .executeCommand(answer, "wrongsecrets-dotnet")
          .equals("This is correct! Congrats!");
    } catch (Exception e) {
      log.error("Error with executing the guess command, did you run LFS?", e);
      return LFS_ERROR.equals(answer);
    }
  }
}
