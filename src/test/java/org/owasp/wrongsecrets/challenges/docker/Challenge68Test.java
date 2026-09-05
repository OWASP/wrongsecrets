package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge68Test {

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge68();

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("Q7v!mR2#xL9@pT6$wN4&kZ8^cF3*Hs5"));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge68();

    assertThat(challenge.answerCorrect("Q7v!mR2#xL9@pT6$wN4&kZ8^cF3*Hs5")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge68();

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
