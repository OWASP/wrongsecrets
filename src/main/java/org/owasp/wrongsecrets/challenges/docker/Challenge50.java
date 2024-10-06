package org.owasp.wrongsecrets.challenges.docker;

import com.google.api.client.util.Strings;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;

/**
 * This challenge is about finding a secret hardcoded in a dotnet binary. Given that the dotnet
 * executables are very large, you have to download them to your system as they are part of the
 * .gitignore. They are located at <a
 * href="https://github.com/OWASP/wrongsecrets-binaries/releases">The WrongSecrets Binary
 * Releases</a>
 */
public class Challenge50 implements Challenge {
  private final BinaryExecutionHelper binaryExecutionHelper;
  private String correctAnswer;

  public Challenge50() {
    this.binaryExecutionHelper = new BinaryExecutionHelper(50, new MuslDetectorImpl());
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    if (Strings.isNullOrEmpty(correctAnswer)) {
      this.correctAnswer = binaryExecutionHelper.executeCommand("", "wrongsecrets-dotnet");
    }
    return new Spoiler(correctAnswer);
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    if (Strings.isNullOrEmpty(correctAnswer)) {
      this.correctAnswer = binaryExecutionHelper.executeCommand("", "wrongsecrets-dotnet");
    }
    return correctAnswer.equals(answer);
  }
}
