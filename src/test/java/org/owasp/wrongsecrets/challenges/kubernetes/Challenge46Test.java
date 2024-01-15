package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Challenge46Test {

  @Test
  void spoilerShouldGiveAnswerWithVault() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("answer");
    var challenge = new Challenge46(vaultPassword, "");
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void spoilerShouldGiveAnswer() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("");
    var challenge = new Challenge46(vaultPassword, "answer");
    assertThat(challenge.spoiler().solution()).isEqualTo("answer");
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("answer");
    var challenge = new Challenge46(vaultPassword, "");
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
