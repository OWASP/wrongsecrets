package org.owasp.wrongsecrets.challenges.cloud;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Challenge10Test {

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

    var challenge = new Challenge10(dir.toString(), "test", "wrongsecret-2");

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isTrue();
  }

  @Test
  void solveChallenge10WithoutAWSFile(@TempDir Path dir) {
    var challenge = new Challenge10(dir.toString(), "test", "wrongsecret-2");

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isFalse();
  }
}
