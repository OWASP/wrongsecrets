package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge3Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge3("test");

    Assertions.assertThat(challenge.spoiler()).isEqualTo(new Spoiler("test"));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge3("test");

    Assertions.assertThat(challenge.answerCorrect("test")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge3("test");

    Assertions.assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
