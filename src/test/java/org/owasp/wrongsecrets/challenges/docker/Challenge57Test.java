package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class Challenge57Test {

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge57();
    assertThat(challenge.answerCorrect("WRONG_SECRETS_LLM_HIDDEN_INSTRUCTION_2024")).isTrue();
  }

  @Test
  void wrongAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge57();
    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge57();
    assertThat(challenge.spoiler().solution())
        .isEqualTo("WRONG_SECRETS_LLM_HIDDEN_INSTRUCTION_2024");
  }
}
