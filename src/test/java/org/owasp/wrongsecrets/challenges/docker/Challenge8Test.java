package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class Challenge8Test {

  @Test
  void spoilerShouldRevealAnswerAndSolveAnswerWhenRandom() {
    var challenge = new Challenge8("");

    assertThat(challenge.spoiler().solution().length()).isEqualTo(10);
    assertThat(challenge.answerCorrect((challenge.spoiler().solution()))).isTrue();
    assertThat(challenge.spoiler().solution()).isNotEmpty();
  }

  @Test
  void spoilerShouldRevealAnswerAndSolveAnswerWhenNotRandom() {
    var challenge = new Challenge8("1234567890");

    assertThat(challenge.spoiler().solution().length()).isEqualTo(10);
    assertThat(challenge.answerCorrect((challenge.spoiler().solution()))).isTrue();
    assertThat(challenge.spoiler().solution()).isEqualTo("1234567890");
  }
}
