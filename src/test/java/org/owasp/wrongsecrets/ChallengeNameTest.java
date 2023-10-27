package org.owasp.wrongsecrets;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.definitions.ChallengeName;

class ChallengeNameTest {

  @Test
  void shouldMatchOnDigit() {
    Assertions.assertThat(new ChallengeName("Challenge 11", "challenge-11").partialMatches("11"))
        .isTrue();
  }

  @Test
  void shouldNotMatch() {
    Assertions.assertThat(
            new ChallengeName("Challenge 11", "challenge-11").partialMatches("no digit"))
        .isFalse();
  }
}
