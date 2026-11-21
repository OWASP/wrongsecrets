package org.owasp.wrongsecrets.challenges.cloud;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.definitions.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Challenge78RegistrationTest {

  @Autowired private Challenges challenges;

  @Test
  void challenge78ShouldBeRegisteredForTheAwsEnvironment() {
    var definition = challenges.findByShortName("challenge-78").orElseThrow();

    assertThat(challenges.getChallenge(definition)).hasSize(1);
    assertThat(challenges.getChallenge(definition).getFirst()).isInstanceOf(Challenge78.class);
    assertThat(definition.supportedEnvironments())
        .extracting(Environment::name)
        .containsExactly("aws");
    assertThat(definition.difficulty().difficulty()).isEqualTo("normal");
    assertThat(definition.category().category()).isEqualTo("Terraform");
    assertThat(definition.ctf().enabled()).isTrue();
  }

  @Test
  void challenge78ShouldShipExplanationHintAndReasonWithoutTheAnswer() {
    var definition = challenges.findByShortName("challenge-78").orElseThrow();
    var challenge = challenges.getChallenge(definition).getFirst();
    var solution = challenge.spoiler().solution();
    var source = definition.sources().getFirst();

    var explanation = source.explanation().contents().get();
    var hint = source.hint().contents().get();
    var reason = source.reason().contents().get();

    assertThat(explanation).isNotBlank();
    assertThat(hint).isNotBlank();
    assertThat(reason).isNotBlank();
    assertThat(explanation).doesNotContain(solution);
    assertThat(hint).doesNotContain(solution);
    assertThat(reason).doesNotContain(solution);
    assertThat(challenge.answerCorrect(solution)).isTrue();
    assertThat(challenge.answerCorrect("not-the-private-key")).isFalse();
  }
}
