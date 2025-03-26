package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge54Test {

  @Test
  void rightAnswerShouldSolveChallenge() throws Exception {
    var challenge = new Challenge54();

    String clearSecret = "(<:GITIGN0RE_SECRET:>)";
    String encryptedInput = Challenge54.encryptAES(clearSecret);

    assertThat(challenge.answerCorrect(encryptedInput)).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() throws Exception {
    var challenge = new Challenge54();

    String wrongSecret = "wrong answer";
    String encryptedInput = Challenge54.encryptAES(wrongSecret);

    assertThat(challenge.answerCorrect(encryptedInput)).isFalse();
  }
}
