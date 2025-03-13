package org.owasp.wrongsecrets.challenges;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class Challenge54Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge54();
    Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
    Assertions.assertThat(challenge.solved(challenge.spoiler().solution())).isTrue();
  }
}