package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
class Challenge41Test {
  @Mock private ScoreCard scoreCard;

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge41(scoreCard, "dGVzdA==");
    Assertions.assertThat(challenge.spoiler().solution()).isEqualTo("test");
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge18(scoreCard, "dGVzdA==");
    Assertions.assertThat(challenge.solved("test")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge41(scoreCard, "dGVzdA==");
    Assertions.assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
