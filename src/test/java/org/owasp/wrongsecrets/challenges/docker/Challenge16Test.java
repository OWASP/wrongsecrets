package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge16Test {

  @Test
  void solveChallenge16WithoutFile(@TempDir Path dir) {
    var challenge = new Challenge16(dir.toString());

    assertThat(challenge.answerCorrect("secretvalueWitFile")).isFalse();
    assertThat(challenge.answerCorrect("if_you_see_this_please_use_docker_instead")).isTrue();
  }

  @Test
  void solveChallenge16WithMNTFile(@TempDir Path dir) throws Exception {
    var testFile = new File(dir.toFile(), "secondkey.txt");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);

    var challenge = new Challenge16(dir.toString());

    assertThat(challenge.answerCorrect("secretvalueWitFile")).isTrue();
  }

  @Test
  void spoilShouldReturnCorrectAnswer(@TempDir Path dir) throws IOException {
    var testFile = new File(dir.toFile(), "secondkey.txt");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);

    var challenge = new Challenge16(dir.toString());

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("secretvalueWitFile"));
  }
}
