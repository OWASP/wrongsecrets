package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Challenge46Test {

  @Test
  void spoilerShouldGiveAnswerWithVault() {
    var vaultInjected = new Vaultinjected();
    vaultInjected.setValue("answer");
    var challenge = new Challenge46(vaultInjected, "");
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void spoilerShouldGiveAnswer() {
    var vaultInjected = new Vaultinjected();
    vaultInjected.setValue("");
    var challenge = new Challenge46(vaultInjected, "answer");
    assertThat(challenge.spoiler().solution()).isEqualTo("answer");
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var vaultInjected = new Vaultinjected();
    vaultInjected.setValue("answer");
    var challenge = new Challenge46(vaultInjected, "");
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
