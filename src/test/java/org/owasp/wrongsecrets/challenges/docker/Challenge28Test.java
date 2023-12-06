package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge28Test {

  private final String secretKey =
      new String(Hex.decode("61736466647075595549616462616f617364706130376b6a32303033"));

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge28();

    assertThat(challenge.spoiler()).isEqualTo(new Spoiler(secretKey));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge28();

    assertThat(challenge.answerCorrect(secretKey)).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge28();

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
