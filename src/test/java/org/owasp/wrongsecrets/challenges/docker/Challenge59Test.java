package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge59Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge59();

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("telegram_secret_found_in_channel"));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge59();

    assertThat(challenge.answerCorrect("telegram_secret_found_in_channel")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge59();

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}