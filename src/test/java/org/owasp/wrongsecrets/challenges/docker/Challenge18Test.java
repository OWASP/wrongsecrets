package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge18Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge18("dGVzdA==");

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("test"));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge18("dGVzdA==");

    assertThat(challenge.answerCorrect("test")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge18("dGVzdA==");

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
