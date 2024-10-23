package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Challenge51Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge51("initsecret");
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge51("initsecret");

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
