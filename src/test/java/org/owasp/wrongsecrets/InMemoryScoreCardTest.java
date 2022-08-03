package org.owasp.wrongsecrets;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.challenges.Challenge;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryScoreCardTest {

    @Mock
    private Challenge challenge1;

    @Mock
    private Challenge challenge2;

    @Test
    void whenOneChallengeSolvedPointsShouldBeCalculatedCorrectly() {
        when(challenge1.difficulty()).thenReturn(2);
        var scoring = new InMemoryScoreCard(2);
        scoring.completeChallenge(challenge1);

        Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(250);
    }

    @Test
    void solvingAllChallengesShouldCalculateMaxPoints() {
        when(challenge1.difficulty()).thenReturn(1);
        when(challenge2.difficulty()).thenReturn(3);
        var scoring = new InMemoryScoreCard(2);
        scoring.completeChallenge(challenge1);
        scoring.completeChallenge(challenge2);

        Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(550);
    }

}
