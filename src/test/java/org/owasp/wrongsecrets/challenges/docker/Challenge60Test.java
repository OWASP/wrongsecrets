package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge60Test {

  @Test
  void spoilerShouldReturnSecret() {
    var challenge = new Challenge60("MCPStolenSecret42!");
    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("MCPStolenSecret42!"));
  }

  @Test
  void answerCorrectShouldReturnTrueForCorrectAnswer() {
    var challenge = new Challenge60("MCPStolenSecret42!");
    assertThat(challenge.answerCorrect("MCPStolenSecret42!")).isTrue();
  }

  @Test
  void answerCorrectShouldReturnFalseForIncorrectAnswer() {
    var challenge = new Challenge60("MCPStolenSecret42!");
    assertThat(challenge.answerCorrect("wronganswer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(null)).isFalse();
  }

  @Test
  void answerCorrectShouldTrimWhitespace() {
    var challenge = new Challenge60("MCPStolenSecret42!");
    assertThat(challenge.answerCorrect("  MCPStolenSecret42!  ")).isTrue();
  }
}
