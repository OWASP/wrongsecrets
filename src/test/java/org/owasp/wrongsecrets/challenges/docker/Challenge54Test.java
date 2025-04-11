package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge54Test {

  @Test
  void rightAnswerShouldSolveChallenge() throws Exception {
    var challenge = new Challenge54();

    String clearSecret = "(<:GITIGN0RE_SECRET:>)";

    assertThat(challenge.answerCorrect(clearSecret)).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() throws Exception {
    var challenge = new Challenge54();

    String wrongSecret = "wrong answer";

    assertThat(challenge.answerCorrect(wrongSecret)).isFalse();
  }
}
