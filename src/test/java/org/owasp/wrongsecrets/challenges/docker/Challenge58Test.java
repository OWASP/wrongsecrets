package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge58Test {

  @Test
  void spoilerShouldReturnCorrectAnswer() {
    var challenge = new Challenge58();
    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("SuperSecretDB2024!"));
  }

  @Test
  void answerCorrectShouldReturnTrueForCorrectAnswer() {
    var challenge = new Challenge58();
    assertThat(challenge.answerCorrect("SuperSecretDB2024!")).isTrue();
  }

  @Test
  void answerCorrectShouldReturnFalseForIncorrectAnswer() {
    var challenge = new Challenge58();
    assertThat(challenge.answerCorrect("wronganswer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(null)).isFalse();
  }

  @Test
  void answerCorrectShouldTrimWhitespace() {
    var challenge = new Challenge58();
    assertThat(challenge.answerCorrect("  SuperSecretDB2024!  ")).isTrue();
  }

  @Test
  void simulateDatabaseConnectionErrorShouldExposeConnectionString() {
    var challenge = new Challenge58();
    String errorMessage = challenge.simulateDatabaseConnectionError();

    // Verify that the error message contains the exposed connection string
    assertThat(errorMessage).contains("jdbc:postgresql://db.example.com:5432/userdb");
    assertThat(errorMessage).contains("user=dbadmin");
    assertThat(errorMessage).contains("password=SuperSecretDB2024!");
    assertThat(errorMessage).contains("Database connection failed");
  }
}
