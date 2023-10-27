package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.owasp.wrongsecrets.ScoreCard;

class Challenge38Test {
  @Mock private ScoreCard scoreCard;

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge38();
    Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge38();
    Assertions.assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
