package org.owasp.wrongsecrets.challenges.docker;

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
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;

@ExtendWith(MockitoExtension.class)
class Challenge16Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void solveChallenge16WithoutFile(@TempDir Path dir) {
    var challenge = new Challenge16(scoreCard, dir.toString());

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isFalse();
    Assertions.assertThat(challenge.answerCorrect("if_you_see_this_please_use_docker_instead"))
        .isTrue();
  }

  @Test
  void solveChallenge16WithMNTFile(@TempDir Path dir) throws Exception {
    var testFile = new File(dir.toFile(), "secondkey.txt");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);

    var challenge = new Challenge16(scoreCard, dir.toString());

    Assertions.assertThat(challenge.answerCorrect("secretvalueWitFile")).isTrue();
  }

  @Test
  void spoilShouldReturnCorrectAnswer(@TempDir Path dir) throws IOException {
    var testFile = new File(dir.toFile(), "secondkey.txt");
    var secret = "secretvalueWitFile";
    Files.writeString(testFile.toPath(), secret);

    var challenge = new Challenge16(scoreCard, dir.toString());

    Assertions.assertThat(challenge.spoiler()).isEqualTo(new Spoiler("secretvalueWitFile"));
  }
}
