package org.owasp.wrongsecrets;

import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Difficulty;

@ExtendWith(MockitoExtension.class)
class InMemoryScoreCardTest {

  @Mock private Challenge challenge1;

  @Mock private Challenge challenge2;

  @Test
  void whenOneChallengeSolvedPointsShouldBeCalculatedCorrectly() {
    when(challenge1.difficulty()).thenReturn(Difficulty.NORMAL);
    var scoring = new InMemoryScoreCard(2);
    scoring.completeChallenge(challenge1);

    Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(250);
  }

  @Test
  void solvingAllChallengesShouldCalculateMaxPoints() {
    when(challenge1.difficulty()).thenReturn(Difficulty.EASY);
    when(challenge2.difficulty()).thenReturn(Difficulty.HARD);
    var scoring = new InMemoryScoreCard(2);
    scoring.completeChallenge(challenge1);
    scoring.completeChallenge(challenge2);

    Assertions.assertThat(scoring.getTotalReceivedPoints()).isEqualTo(550);
  }
}
