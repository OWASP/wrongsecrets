package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge27Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge =
        new Challenge27("gYPQPfb0TUgWK630tHCWGwwME6IWtPWA51eU0Qpb9H7/lMlZPdLGZWmYE83YmEDmaEvFr2hX");
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
