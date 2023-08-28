package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
class Challenge36Test {
  @Mock private ScoreCard scoreCard;

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge36(scoreCard);
    Assertions.assertThat(challenge.solved("igothackedby")).isFalse();
    Assertions.assertThat(challenge.solved("igothackedby2")).isFalse();
    Assertions.assertThat(challenge.solved("igothigothackedbyackedby")).isFalse();
    Assertions.assertThat(challenge.solved("igothigotesecret")).isFalse();
    Assertions.assertThat(challenge.solved("thisisincorrect")).isFalse();
    Assertions.assertThat(challenge.solved(challenge.spoiler().solution())).isTrue();
  }
}
