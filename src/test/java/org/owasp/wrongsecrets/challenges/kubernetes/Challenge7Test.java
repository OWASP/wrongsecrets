package org.owasp.wrongsecrets.challenges.kubernetes;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.owasp.wrongsecrets.ScoreCard;

class Challenge7Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void spoilerShouldGiveAnswerWithVault() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("testvalue");
    var challenge = new Challenge7(scoreCard, vaultPassword, "");
    Assertions.assertThat(challenge.spoiler().solution()).isEqualTo("testvalue");
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void spoilerShouldGiveAnswerPreinit() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("");
    var challenge = new Challenge7(scoreCard, vaultPassword, "testvalue");
    Assertions.assertThat(challenge.spoiler().solution()).isEqualTo("testvalue");
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var vaultPassword = new Vaultpassword();
    vaultPassword.setPassword("testvalue");
    var challenge = new Challenge7(scoreCard, vaultPassword, "");
    Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
  }
}
