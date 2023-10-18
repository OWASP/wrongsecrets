package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;

@ExtendWith(MockitoExtension.class)
class Challenge1Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge = new Challenge1(scoreCard);

    Assertions.assertThat(challenge.spoiler())
        .isEqualTo(new Spoiler(WrongSecretsConstants.password));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge = new Challenge1(scoreCard);

    Assertions.assertThat(challenge.solved(WrongSecretsConstants.password)).isTrue();
    Mockito.verify(scoreCard).completeChallenge(challenge);
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge1(scoreCard);

    Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
    Mockito.verifyNoInteractions(scoreCard);
  }
}
