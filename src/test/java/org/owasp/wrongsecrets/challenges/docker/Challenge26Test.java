package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
class Challenge26Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge =
        new Challenge26(
            scoreCard,
            "gbU5thfgy8nwzF/qc1Pq59PrJzLB+bfAdTOrx969JZx1CKeG4Sq7v1uUpzyCH/Fo8W8ghdBJJrQORw==");
    Assertions.assertThat(challenge.solved(challenge.spoiler().solution())).isTrue();
    Mockito.verify(scoreCard).completeChallenge(challenge);
  }
}
