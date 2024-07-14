package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class Challenge14Test {

  @Test
  void solveChallenge14() throws URISyntaxException {
    URI uri = getClass().getClassLoader().getResource("alibabacreds.kdbx").toURI();
    String filePath = Paths.get(uri).toString();
    var challenge = new Challenge14("welcome123", "doesnotwork", filePath);

    assertThat(challenge.answerCorrect("doesnotwork")).isFalse();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void solveChallenge14WithNoFileOrPass() {
    var challenge = new Challenge14("incorrectpass", "doesnotwork", "nofile is here");

    assertThat(challenge.answerCorrect("doesnotwork")).isTrue();
    assertThat(challenge.spoiler().solution()).isEqualTo("doesnotwork");
  }
}
