package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import org.junit.jupiter.api.Test;

class Challenge27Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge =
        new Challenge27("gYPQPfb0TUgWK630tHCWGwwME6IWtPWA51eU0Qpb9H7/lMlZPdLGZWmYE83YmEDmaEvFr2hX");
    assertThat(challenge.spoiler().solution()).isNotEqualTo(DECRYPTION_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
