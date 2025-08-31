package org.owasp.wrongsecrets.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NavigatorTest {

  @Autowired private ChallengeDefinitionsConfiguration challengeDefinitionsConfiguration;

  @Test
  void navigatePreviousWhenOnFirstChallenge() {
    var navigation =
        new Navigator(
            challengeDefinitionsConfiguration.challenges(),
            challengeDefinitionsConfiguration.challenges().getFirst());

    assertThat(navigation.previous()).isEmpty();
  }

  @Test
  void navigateNextWhenOnLastChallenge() {
    var challenges = challengeDefinitionsConfiguration.challenges();
    var navigation = new Navigator(challenges, challenges.getLast());

    assertThat(navigation.next()).isEmpty();
  }

  @Test
  void navigatePreviousAndNextOnSecondChallenge() {
    var challenges = challengeDefinitionsConfiguration.challenges();
    var first = challenges.getFirst();
    var second = challenges.get(1);
    var third = challenges.get(2);

    var navigation = new Navigator(challengeDefinitionsConfiguration.challenges(), second);

    assertThat(navigation.previous()).hasValue(first);
    assertThat(navigation.next()).hasValue(third);
  }
}
