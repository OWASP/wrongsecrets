package org.owasp.wrongsecrets.challenges.docker;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
class Challenge14Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void solveChallenge14() throws URISyntaxException {
    URI uri = getClass().getClassLoader().getResource("alibabacreds.kdbx").toURI();
    String filePath = Paths.get(uri).toString();
    var challenge = new Challenge14(scoreCard, "welcome123", "doesnotwork", filePath);
    Assertions.assertThat(challenge.answerCorrect("doesnotwork")).isFalse();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void solveChallenge14WithNoFileOrPass() {
    var challenge = new Challenge14(scoreCard, "incorrectpass", "doesnotwork", "nofile is here");
    Assertions.assertThat(challenge.answerCorrect("doesnotwork")).isTrue();
    Assertions.assertThat(challenge.spoiler().solution()).isEqualTo("doesnotwork");
  }
}
