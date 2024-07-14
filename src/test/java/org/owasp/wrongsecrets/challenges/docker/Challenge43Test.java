package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge43Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge43();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge43();

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
