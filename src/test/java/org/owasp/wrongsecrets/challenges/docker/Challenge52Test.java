package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class Challenge52Test {

  @Test
  void rightAnswerShouldSolveChallenge(@TempDir Path dir) throws IOException {
    var testFile = new File(dir.toFile(), "secret.txt");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);
    var challenge = new Challenge52(dir.toString());
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    assertThat(challenge.answerCorrect("secretvalueWitFile")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge(@TempDir Path dir) {
    var challenge = new Challenge51("initsecret");

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
