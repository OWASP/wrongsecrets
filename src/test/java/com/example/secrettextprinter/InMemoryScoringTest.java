package com.example.secrettextprinter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class InMemoryScoringTest {

    @Test
    void whenOneChallengeSolvedPointsShouldBeCalculatedCorrectly() {
        var scoring = new InMemoryScoring(2);
        scoring.completeChallenge(1);

        Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(50);
    }

    @Test
    void solvingAllChallengesShouldCalculateMaxPoints() {
        var scoring = new InMemoryScoring(2);
        scoring.completeChallenge(1);
        scoring.completeChallenge(2);

        Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(100);
    }

}
