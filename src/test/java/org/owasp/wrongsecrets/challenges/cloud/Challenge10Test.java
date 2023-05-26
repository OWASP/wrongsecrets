package org.owasp.wrongsecrets.challenges.cloud;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
class Challenge10Test {

  @Mock private ScoreCard scoreCard;
  @Mock private RuntimeEnvironment runtimeEnvironment;

  @Test
  void solveChallenge10WithAWSFile(@TempDir Path dir) throws Exception {
    var testFile = new File(dir.toFile(), "wrongsecret-2");
    var secret = "secretvalueWitFile";
    Files.writeString(
        testFile.toPath(),
        secret,
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);

    var challenge =
        new Challenge10(scoreCard, dir.toString(), "test", "wrongsecret-2", runtimeEnvironment);

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isTrue();
  }

  @Test
  void solveChallenge10WithoutAWSFile(@TempDir Path dir) {
    var challenge =
        new Challenge10(scoreCard, dir.toString(), "test", "wrongsecret-2", runtimeEnvironment);

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isFalse();
  }

  @Test
  void whenGCPEnvGCPDocumentationShouldBeReturned() {
    Mockito.when(runtimeEnvironment.getRuntimeEnvironment())
        .thenReturn(RuntimeEnvironment.Environment.GCP);

    var challenge = new Challenge10(scoreCard, "", "test", "wrongsecret-2", runtimeEnvironment);

    Assertions.assertThat(challenge.getExplanation()).isEqualTo("challenge10-gcp");
  }

  @Test
  void whenAWSEnvAWSDocumentationShouldBeReturned() {
    Mockito.when(runtimeEnvironment.getRuntimeEnvironment())
        .thenReturn(RuntimeEnvironment.Environment.AWS);

    var challenge = new Challenge10(scoreCard, "", "test", "wrongsecret-2", runtimeEnvironment);

    Assertions.assertThat(challenge.getExplanation()).isEqualTo("challenge10");
  }
}
