package org.owasp.wrongsecrets.challenges.docker;

import com.google.api.client.util.Strings;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.MuslDetectorImpl;

/** challenge about dotnet binaries. Please download them from the wrongsecrets-binaries release. */
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
