package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

class Challenge76ControllerTest {

  private static final String DATABASE_LOCATION =
      "challenges/challenge-76/wrongsecrets-users.sqlite";

  @Test
  void shouldServeTheUserDatabaseAsDownload() {
    var controller = new Challenge76Controller(new ClassPathResource(DATABASE_LOCATION));

    var response = controller.userDatabase();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).hasToString("application/octet-stream");
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody())
        .startsWith("SQLite format 3".getBytes(StandardCharsets.US_ASCII));
  }

  @Test
  void servedUserDatabaseShouldContainTheAnswerOfEveryTargetUser() {
    var controller = new Challenge76Controller(new ClassPathResource(DATABASE_LOCATION));
    var challenge = new Challenge76(new ClassPathResource(DATABASE_LOCATION));

    assertThat(challenge.getTargetUsername()).isNotBlank();
    assertThat(controller.userDatabase().getBody()).isNotNull();
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void shouldReturnServerErrorWhenTheDatabaseIsMissing() {
    var controller =
        new Challenge76Controller(
            new ClassPathResource("challenges/challenge-76/does-not-exist.sqlite"));

    assertThat(controller.userDatabase().getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
