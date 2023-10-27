package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper.ERROR_EXECUTION;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge22Test {

  @Test
  void spoilerShouldNotCrash() {
    var challenge = new Challenge22();

    assertThat(challenge.spoiler()).isNotEqualTo(new Spoiler(ERROR_EXECUTION));
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
