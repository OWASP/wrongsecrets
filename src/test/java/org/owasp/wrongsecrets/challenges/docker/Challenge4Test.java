package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge4Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge4("test");

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("test"));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge4("test");

    assertThat(challenge.answerCorrect("test")).isTrue();
  }

  @Test
  void rightAnswerWithoutQuotesShouldSolveChallenge() {
    var challenge = new Challenge4("'test'");

    assertThat(challenge.answerCorrect("test")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge4("test");

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
