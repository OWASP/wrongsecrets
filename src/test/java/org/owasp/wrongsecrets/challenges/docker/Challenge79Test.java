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
class Challenge79Test {

  private static final String EXPECTED_SECRET =
      "Forking this repo forked my encrypted secret and its key";

  @Autowired private Challenges challenges;

  @Test
  void spoilerShouldGiveTheDecryptedSecret() {
    var challenge = new Challenge79();

    assertThat(challenge.spoiler().solution()).isEqualTo(EXPECTED_SECRET);
    assertThat(challenge.answerCorrect(EXPECTED_SECRET)).isTrue();
  }

  @Test
  void incorrectAnswersShouldNotSolveChallenge() {
    var challenge = new Challenge79();

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(Challenge79.CIPHERTEXT)).isFalse();
  }

  @Test
  void workflowShouldShipTheSameCiphertextKeyAndIv() throws IOException {
    var workflow = Files.readString(Path.of(".github/workflows/challenge79.yml"));

    assertThat(workflow)
        .contains("CHALLENGE_79_CIPHERTEXT: \"" + Challenge79.CIPHERTEXT + "\"")
        .contains("CHALLENGE_79_KEY: \"" + Challenge79.KEY + "\"")
        .contains("CHALLENGE_79_IV: \"" + Challenge79.IV + "\"")
        .doesNotContain("secrets.");
  }

  @Test
  void challenge79ShouldBeRegisteredAndSolveable() {
    var definition = challenges.findByShortName("challenge-79");

    assertThat(definition).isPresent();
    var challenge = challenges.getChallenge(definition.get()).getFirst();

    assertThat(challenge).isInstanceOf(Challenge79.class);
    assertThat(challenge.answerCorrect(EXPECTED_SECRET)).isTrue();
  }
}
