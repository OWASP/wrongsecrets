package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;

class Challenge62Test {

  @Test
  void spoilerShouldReturnConfiguredSecret() {
    var challenge = new Challenge62("my_google_drive_secret_42");
    assertThat(challenge.spoiler()).isEqualTo(new Spoiler("my_google_drive_secret_42"));
  }

  @Test
  void answerCorrectShouldReturnTrueForCorrectAnswer() {
    var challenge = new Challenge62("my_google_drive_secret_42");
    assertThat(challenge.answerCorrect("my_google_drive_secret_42")).isTrue();
  }

  @Test
  void answerCorrectShouldReturnFalseForIncorrectAnswer() {
    var challenge = new Challenge62("my_google_drive_secret_42");
    assertThat(challenge.answerCorrect("wronganswer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
    assertThat(challenge.answerCorrect(null)).isFalse();
  }

  @Test
  void answerCorrectShouldTrimWhitespace() {
    var challenge = new Challenge62("my_google_drive_secret_42");
    assertThat(challenge.answerCorrect("  my_google_drive_secret_42  ")).isTrue();
  }

  @Test
  void defaultValueShouldBeUsedWhenNotConfigured() {
    var challenge =
        new Challenge62("if_you_see_this_configure_the_google_service_account_properly");
    assertThat(challenge.spoiler().solution())
        .isEqualTo("if_you_see_this_configure_the_google_service_account_properly");
  }
}
