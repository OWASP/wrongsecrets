package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import org.junit.jupiter.api.Test;

public class Challenge32Test {

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge32();
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.spoiler().solution()).isNotEqualTo(DECRYPTION_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge32();
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
