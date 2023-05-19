package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
class Challenge25Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge =
        new Challenge25(
            scoreCard,
            "dQMhBe8oLxIdGLcxPanDLS++srED/x05P+Ph9PFZKlL2K42vXi7Vtbh3/N90sGT087W7ARURZg==");
    Assertions.assertThat(challenge.solved(challenge.spoiler().solution())).isTrue();
    Mockito.verify(scoreCard).completeChallenge(challenge);
  }
}
