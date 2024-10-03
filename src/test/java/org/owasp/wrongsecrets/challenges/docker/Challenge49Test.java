package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class Challenge49Test {

  @Test
  void spoilerShouldGiveAnswer() {
    var challenge = new Challenge49("uz5cIFm0hW3LtWaqEX0S/Q==", "MTIzNDU=");
    Assertions.assertThat(challenge.spoiler().solution()).isEqualTo("12345");
  }

  @Test
  void correctPinShouldSolveChallenge() {
    var challenge = new Challenge49("uz5cIFm0hW3LtWaqEX0S/Q==", "MTIzNDU=");
    Assertions.assertThat(challenge.answerCorrect("12345")).isTrue();
  }

  @Test
  void nonIntegerPinShouldNotSolveChallenge() {
    var challenge = new Challenge49("uz5cIFm0hW3LtWaqEX0S/Q==", "MTIzNDU=");
    Assertions.assertThat(challenge.answerCorrect("abcde")).isFalse();
  }

  @Test
  void incorrectPinShouldNotSolveChallenge() {
    var challenge = new Challenge49("uz5cIFm0hW3LtWaqEX0S/Q==", "MTIzNDU=");
    Assertions.assertThat(challenge.answerCorrect("1234")).isFalse();
  }

  @Test
  void pinGreaterThan99999ShouldNotSolveChallenge() {
    var challenge = new Challenge49("uz5cIFm0hW3LtWaqEX0S/Q==", "MTIzNDU=");
    Assertions.assertThat(challenge.answerCorrect("123456")).isFalse();
  }

  @Test
  void pinLesserThan0ShouldNotSolveChallenge() {
    var challenge = new Challenge49("uz5cIFm0hW3LtWaqEX0S/Q==", "MTIzNDU=");
    Assertions.assertThat(challenge.answerCorrect("-123456")).isFalse();
  }
}
