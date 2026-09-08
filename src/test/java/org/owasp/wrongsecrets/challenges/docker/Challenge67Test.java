package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge67Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge67("test-secret");

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("test-secret"));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge67("test-secret");

    assertThat(challenge.answerCorrect("test-secret")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge67("test-secret");

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
