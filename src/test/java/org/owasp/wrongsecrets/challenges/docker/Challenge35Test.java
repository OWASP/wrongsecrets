package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.owasp.wrongsecrets.ScoreCard;

class Challenge35Test {
  @Mock private ScoreCard scoreCard;

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge35(scoreCard);
    Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge35(scoreCard);
    Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
  }
}
