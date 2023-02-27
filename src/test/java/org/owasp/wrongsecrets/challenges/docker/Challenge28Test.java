package org.owasp.wrongsecrets.challenges.docker;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

@ExtendWith(MockitoExtension.class)
class Challenge28Test {

    @Mock
    private ScoreCard scoreCard;

    @Test
    void rightAnswerShouldSolveChallenge() {
        var challenge = new Challenge28(scoreCard);
        Assertions.assertThat(challenge.solved("wrong answer")).isFalse();
        Assertions.assertThat(challenge.solved(challenge.spoiler().solution())).isTrue();

    }



}