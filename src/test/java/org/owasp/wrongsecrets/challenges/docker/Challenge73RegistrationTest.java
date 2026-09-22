package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Challenge73RegistrationTest {

  private static final String EXPECTED_TOKEN = "Ag3nt_C0nt3xt_L3ak3d_T0k3n!";

  @Autowired private Challenges challenges;

  @Test
  void challenge73ShouldBeRegisteredAndDiscoverable() {
    var definition = challenges.findByShortName("challenge-73");

    assertThat(definition).isPresent();
    assertThat(challenges.getChallenge(definition.get())).hasSize(1);
    assertThat(challenges.getChallenge(definition.get()).getFirst())
        .isInstanceOf(Challenge73.class);
  }

  @Test
  void registeredChallenge73ShouldExposeTheLeakedToken() {
    var definition = challenges.findByShortName("challenge-73").orElseThrow();

    var challenge = challenges.getChallenge(definition).getFirst();

    assertThat(challenge.spoiler().solution()).isEqualTo(EXPECTED_TOKEN);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }
}
