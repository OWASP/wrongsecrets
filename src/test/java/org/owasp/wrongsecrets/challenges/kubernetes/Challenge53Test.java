package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Challenge53Test {

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge53();
    assertThat(challenge.spoiler().solution()).isEqualTo("K8S_DEBUG_SECRET";
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge53();
    assertThat(challenge.answerCorrect("K8S_DEBUG_SECRET")).isFalse();
  }
}
