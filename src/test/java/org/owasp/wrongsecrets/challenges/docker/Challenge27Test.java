package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
class Challenge27Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge =
        new Challenge27(
            scoreCard, "gYPQPfb0TUgWK630tHCWGwwME6IWtPWA51eU0Qpb9H7/lMlZPdLGZWmYE83YmEDmaEvFr2hX");
    Assertions.assertThat(challenge.solved(challenge.spoiler().solution())).isTrue();
    Mockito.verify(scoreCard).completeChallenge(challenge);
  }
}
