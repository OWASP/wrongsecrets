package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge1Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge1();

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler(WrongSecretsConstants.password));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge1();

    assertThat(challenge.answerCorrect(WrongSecretsConstants.password)).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge1();

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
