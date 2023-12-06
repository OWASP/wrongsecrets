package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Challenge34Test {

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge34();
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge34();
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
