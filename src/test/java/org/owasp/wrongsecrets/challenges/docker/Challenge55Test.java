package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.challenges.Spoiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class Challenge55Test {

  @Test
  void solveChallenge55WithoutFile(@TempDir Path dir) {
    var challenge = new Challenge55(dir.toString());

      assertThat(challenge.answerCorrect("secretvalueWitFile")).isFalse();
      assertThat(challenge.answerCorrect(Challenges.ErrorResponses.FILE_MOUNT_ERROR)).isTrue();
  }

  @Test
  void soslveChallenge55WithFile(@TempDir Path dir) throws Exception {
      var testFile = new File(dir.toFile(), "wrongsecrets.keys");
      var secret = "some privatey key";
      Files.writeString(testFile.toPath(), secret);

      var challenge = new Challenge55(dir.toString());
      assertThat(challenge.answerCorrect("some privatey key")).isTrue();
  }
    @Test
    void spoilShouldReturnCorrectAnswer(@TempDir Path dir) throws IOException {
        var testFile = new File(dir.toFile(), "wrongsecrets.keys");
        var secret = "secretvalueWitFile";
        Files.writeString(testFile.toPath(), secret);

        var challenge = new Challenge55(dir.toString());

        assertThat(challenge.spoiler()).isEqualTo(new Spoiler("secretvalueWitFile"));
    }
}
