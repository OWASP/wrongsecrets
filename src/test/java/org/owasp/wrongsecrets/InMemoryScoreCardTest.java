package org.owasp.wrongsecrets;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class InMemoryScoreCardTest {

    @Test
    void whenOneChallengeSolvedPointsShouldBeCalculatedCorrectly() {
        var scoring = new InMemoryScoreCard(2);
        scoring.completeChallenge(1);

        Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(50);
    }

    @Test
    void solvingAllChallengesShouldCalculateMaxPoints() {
        var scoring = new InMemoryScoreCard(2);
        scoring.completeChallenge(1);
        scoring.completeChallenge(2);

        Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(100);
    }

}
