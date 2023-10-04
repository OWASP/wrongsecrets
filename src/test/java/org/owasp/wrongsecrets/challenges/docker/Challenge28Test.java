package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;

@ExtendWith(MockitoExtension.class)
class Challenge28Test {

  @Mock private ScoreCard scoreCard;
  private final String secretKey =
      new String(Hex.decode("61736466647075595549616462616f617364706130376b6a32303033"));

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge28(scoreCard);

    Assertions.assertThat(challenge.spoiler()).isEqualTo(new Spoiler(secretKey));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge28(scoreCard);

    Assertions.assertThat(challenge.solved(secretKey)).isTrue();
    Mockito.verify(scoreCard).completeChallenge(challenge);
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge28(scoreCard);

    Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
    Mockito.verifyNoInteractions(scoreCard);
  }
}
