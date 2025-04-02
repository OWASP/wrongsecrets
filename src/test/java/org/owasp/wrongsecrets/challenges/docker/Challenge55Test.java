package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge55Test {

  @Test
  void rightAnswerShouldSolveChallenge() throws Exception {
    var challenge = new Challenge55();

    String clearSecret = "(<:SSH_SECRET:>)";
    String encryptedInput = Challenge55.encryptAES(clearSecret);

    assertThat(challenge.answerCorrect(encryptedInput)).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() throws Exception {
    var challenge = new Challenge55();

    String wrongSecret = "wrong answer";
    String encryptedInput = Challenge55.encryptAES(wrongSecret);

    assertThat(challenge.answerCorrect(encryptedInput)).isFalse();
  }
}
