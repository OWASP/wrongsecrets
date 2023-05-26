package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.binaryexecution.BinaryExecutionHelper;

@ExtendWith(MockitoExtension.class)
class Challenge20Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void spoilerShouldNotCrash() {
    var challenge = new Challenge20(scoreCard);

    Assertions.assertThat(challenge.spoiler())
        .isNotEqualTo(new Spoiler(BinaryExecutionHelper.ERROR_EXECUTION));
    Assertions.assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
