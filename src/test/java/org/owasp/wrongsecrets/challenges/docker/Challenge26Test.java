package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import org.junit.jupiter.api.Test;

class Challenge26Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge =
        new Challenge26(
            "gbU5thfgy8nwzF/qc1Pq59PrJzLB+bfAdTOrx969JZx1CKeG4Sq7v1uUpzyCH/Fo8W8ghdBJJrQORw==");
    assertThat(challenge.spoiler().solution()).isNotEqualTo(DECRYPTION_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
