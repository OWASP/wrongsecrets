package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge36Test {
  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge36();
    assertThat(challenge.answerCorrect("igothackedby")).isFalse();
    assertThat(challenge.answerCorrect("igothackedby2")).isFalse();
    assertThat(challenge.answerCorrect("igothigothackedbyackedby")).isFalse();
    assertThat(challenge.answerCorrect("igothigotesecret")).isFalse();
    assertThat(challenge.answerCorrect("thisisincorrect")).isFalse();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
