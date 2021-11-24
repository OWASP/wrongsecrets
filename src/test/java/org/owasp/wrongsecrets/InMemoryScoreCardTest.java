package org.owasp.wrongsecrets;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.challenges.Challenge;

@ExtendWith(MockitoExtension.class)
class InMemoryScoreCardTest {

    @Mock
    private Challenge challenge1;

    @Mock
    private Challenge challenge2;

    @Test
    void whenOneChallengeSolvedPointsShouldBeCalculatedCorrectly() {
        var scoring = new InMemoryScoreCard(2);
        scoring.completeChallenge(challenge1);

        Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(50);
    }

    @Test
    void solvingAllChallengesShouldCalculateMaxPoints() {
        var scoring = new InMemoryScoreCard(2);
        scoring.completeChallenge(challenge1);
        scoring.completeChallenge(challenge2);

        Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(100);
    }

}
