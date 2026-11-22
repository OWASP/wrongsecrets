package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Challenge77Test {

  private static final String EXPECTED_SECRET =
      "Forking this repo forked my encrypted secret and its key";

  @Autowired private Challenges challenges;

  @Test
  void spoilerShouldGiveTheDecryptedSecret() {
    var challenge = new Challenge77();

    assertThat(challenge.spoiler().solution()).isEqualTo(EXPECTED_SECRET);
    assertThat(challenge.answerCorrect(EXPECTED_SECRET)).isTrue();
  }

  @Test
  void incorrectAnswersShouldNotSolveChallenge() {
    var challenge = new Challenge77();

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(Challenge77.CIPHERTEXT)).isFalse();
  }

  @Test
  void workflowShouldShipTheSameCiphertextKeyAndIv() throws IOException {
    var workflow = Files.readString(Path.of(".github/workflows/challenge77.yml"));

    assertThat(workflow)
        .contains("CHALLENGE_77_CIPHERTEXT: \"" + Challenge77.CIPHERTEXT + "\"")
        .contains("CHALLENGE_77_KEY: \"" + Challenge77.KEY + "\"")
        .contains("CHALLENGE_77_IV: \"" + Challenge77.IV + "\"")
        .doesNotContain("secrets.");
  }

  @Test
  void challenge77ShouldBeRegisteredAndSolveable() {
    var definition = challenges.findByShortName("challenge-77");

    assertThat(definition).isPresent();
    var challenge = challenges.getChallenge(definition.get()).getFirst();

    assertThat(challenge).isInstanceOf(Challenge77.class);
    assertThat(challenge.answerCorrect(EXPECTED_SECRET)).isTrue();
  }
}
