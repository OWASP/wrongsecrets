package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge7Test {

  @Test
  void spoilerShouldGiveAnswerWithVault() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("testvalue");
    var challenge = new Challenge7(vaultPassword, "");
    assertThat(challenge.spoiler().solution()).isEqualTo("testvalue");
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void spoilerShouldGiveAnswerPreinit() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("");
    var challenge = new Challenge7(vaultPassword, "testvalue");
    assertThat(challenge.spoiler().solution()).isEqualTo("testvalue");
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("testvalue");
    var challenge = new Challenge7(vaultPassword, "");
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
