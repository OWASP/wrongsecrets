package org.owasp.wrongsecrets.challenges.docker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.ScoreCard;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Challenge15Test {

    @Mock
    private ScoreCard scoreCard;

    @Test
    void solveChallenge15() {
        Challenge15 challenge15 = new Challenge15(scoreCard);
        assertThat(challenge15.spoiler().toString()).contains("aws");
        assertThat(challenge15.answerCorrect(challenge15.spoiler().solution())).isTrue();

    }

}
