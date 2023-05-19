package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;

@ExtendWith(MockitoExtension.class)
class Challenge13Test {

  @Mock private ScoreCard scoreCard;

  @Test
  void spoilerShouldRevealAnswer() {
    var challenge =
        new Challenge13(
            scoreCard,
            "This is not the secret",
            "hRZqOEB0V0kU6JhEXdm8UH32VDAbAbdRxg5RMpo/fA8caUCvJhs=");

    Assertions.assertThat(challenge.spoiler())
        .isEqualTo(
            new Spoiler(
                Base64.getEncoder()
                    .encodeToString(
                        "This is our first key as github secret"
                            .getBytes(StandardCharsets.UTF_8))));
  }

  @Test
  void rightAnswerShouldSolveChallenge() {
    var challenge =
        new Challenge13(
            scoreCard,
            "This is not the secret",
            "hRZqOEB0V0kU6JhEXdm8UH32VDAbAbdRxg5RMpo/fA8caUCvJhs=");

    Assertions.assertThat(
            challenge.solved(
                Base64.getEncoder()
                    .encodeToString(
                        "This is our first key as github secret".getBytes(StandardCharsets.UTF_8))))
        .isTrue();
    Mockito.verify(scoreCard).completeChallenge(challenge);
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge =
        new Challenge13(
            scoreCard,
            "This is not the secret",
            "hRZqOEB0V0kU6JhEXdm8UH32VDAbAbdRxg5RMpo/fA8caUCvJhs=");

    Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
    Mockito.verifyNoInteractions(scoreCard);
  }
}
