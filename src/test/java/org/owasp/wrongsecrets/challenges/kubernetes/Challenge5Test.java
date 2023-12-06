package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge5Test {

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge5("value-from-k8s");
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge5("value-from-k8s");
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
