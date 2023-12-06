package org.owasp.wrongsecrets.challenges.docker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge17Test {

  @Test
  void solveChallenge17WithoutFile(@TempDir Path dir) {
    var challenge = new Challenge17(dir.toString());

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isFalse();
    Assertions.assertThat(challenge.answerCorrect("if_you_see_this_please_use_docker_instead"))
        .isTrue();
  }

  @Test
  void solveChallenge17WithMNTFile(@TempDir Path dir) throws Exception {
    var testFile = new File(dir.toFile(), "thirdkey.txt");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);

    var challenge = new Challenge17(dir.toString());

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isTrue();
  }

  @Test
  void spoilShouldReturnCorrectAnswer(@TempDir Path dir) throws IOException {
    var testFile = new File(dir.toFile(), "thirdkey.txt");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);

    var challenge = new Challenge17(dir.toString());

    Assertions.assertThat(challenge.spoiler()).isEqualTo(new Spoiler("secretvalueWitFile"));
  }
}
