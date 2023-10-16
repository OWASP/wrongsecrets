package org.owasp.wrongsecrets.challenges.kubernetes;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.owasp.wrongsecrets.ScoreCard;

class Challenge5Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge5(scoreCard, "value-from-k8s");
    Assertions.assertThat(challenge.spoiler().solution()).isNotEmpty();
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge5(scoreCard, "value-from-k8s");
    Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
  }
}
