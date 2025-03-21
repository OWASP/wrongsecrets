package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge55Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge55();
    assertThat(challenge.answerCorrect(challenge.getAnswer())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge55();
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
