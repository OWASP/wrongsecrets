package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.docker.authchallenge.Challenge37;

public class Challenge37Test {

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge37("DEFAULT37");
    assertThat(challenge.spoiler().solution()).isNotEmpty();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge37("DEFAULT37");
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
