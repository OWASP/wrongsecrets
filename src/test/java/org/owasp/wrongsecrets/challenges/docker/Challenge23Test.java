package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge23Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge23();

    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
