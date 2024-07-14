package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.EXECUTION_ERROR;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge21Test {

  @Test
  void spoilerShouldNotCrash() {
    var challenge = new Challenge21();

    assertThat(challenge.spoiler()).isNotEqualTo(new Spoiler(EXECUTION_ERROR));
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
