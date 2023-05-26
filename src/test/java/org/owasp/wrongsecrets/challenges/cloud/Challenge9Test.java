package org.owasp.wrongsecrets.challenges.cloud;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;

@ExtendWith(MockitoExtension.class)
class Challenge9Test {

  @Mock private ScoreCard scoreCard;
  @Mock private RuntimeEnvironment runtimeEnvironment;

  @Test
  void solveChallenge9WithoutFile(@TempDir Path dir) {
    var challenge =
        new Challenge9(scoreCard, dir.toString(), "test", "wrongsecret", runtimeEnvironment);

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isFalse();
  }

  @Test
  void solveChallenge9WithAWSFile(@TempDir Path dir) throws Exception {
    var testFile = new File(dir.toFile(), "wrongsecret");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);

    var challenge =
        new Challenge9(scoreCard, dir.toString(), "test", "wrongsecret", runtimeEnvironment);

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isTrue();
  }

  @Test
  void spoilShouldReturnCorrectAnswer(@TempDir Path dir) throws IOException {
    var testFile = new File(dir.toFile(), "wrongsecret");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);

    var challenge =
        new Challenge9(scoreCard, dir.toString(), "test", "wrongsecret", runtimeEnvironment);

    Assertions.assertThat(challenge.spoiler()).isEqualTo(new Spoiler("secretvalueWitFile"));
  }
}
