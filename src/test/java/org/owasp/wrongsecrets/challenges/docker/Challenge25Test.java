package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge25Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge =
        new Challenge25(
            "dQMhBe8oLxIdGLcxPanDLS++srED/x05P+Ph9PFZKlL2K42vXi7Vtbh3/N90sGT087W7ARURZg==");
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
