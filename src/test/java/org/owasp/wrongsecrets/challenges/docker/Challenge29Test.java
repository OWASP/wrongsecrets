package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;

@ExtendWith(MockitoExtension.class)
class Challenge29Test {

  @Mock private ScoreCard scoreCard;
  private final String passcode = new String(Base64.decode("c2RmZzk4YXNkZmc4YW53c2VkZHJmdWE9"));

  @Test
  void spoilerShouldRevealAnswer() throws Exception {
    var challenge = new Challenge29(scoreCard);
    Assertions.assertThat(challenge.spoiler()).isNotEqualTo("decrypt_error");
    Assertions.assertThat(challenge.spoiler()).isEqualTo(new Spoiler(passcode));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge29(scoreCard);

    Assertions.assertThat(challenge.solved(passcode)).isTrue();
    Mockito.verify(scoreCard).completeChallenge(challenge);
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge29(scoreCard);

    Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
    Mockito.verifyNoInteractions(scoreCard);
  }
}
