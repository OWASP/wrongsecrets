package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Challenge41Test {

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge41("dGVzdA==");
    Assertions.assertThat(challenge.spoiler().solution()).isEqualTo("test");
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge18("dGVzdA==");
    Assertions.assertThat(challenge.answerCorrect("test")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge41("dGVzdA==");
    Assertions.assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
