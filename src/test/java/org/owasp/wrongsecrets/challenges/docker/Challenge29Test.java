package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge29Test {

  private final String passcode = new String(Base64.decode("c2RmZzk4YXNkZmc4YW53c2VkZHJmdWE9"));

  @Test
  void spoilerShouldRevealAnswer() throws Exception {
    var challenge = new Challenge29();
    assertThat(challenge.spoiler()).isNotEqualTo("decrypt_error");
    assertThat(challenge.spoiler()).isEqualTo(new Spoiler(passcode));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge29();

    assertThat(challenge.answerCorrect(passcode)).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge29();

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
  }
}
