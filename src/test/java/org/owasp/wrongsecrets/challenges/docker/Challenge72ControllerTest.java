package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

class Challenge72ControllerTest {

  private static final String TRANSCRIPT_LOCATION =
      "challenges/challenge-72/codex-session-transcript.md";

  @Test
  void shouldServeTheTranscriptAsMarkdown() {
    var controller = new Challenge72Controller(new ClassPathResource(TRANSCRIPT_LOCATION));

    var response = controller.codexTranscript();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).hasToString("text/markdown;charset=UTF-8");
    assertThat(response.getBody()).contains("DEPLOY_TOKEN=");
  }

  @Test
  void servedTranscriptShouldContainTheAnswerOfTheChallenge() {
    var controller = new Challenge72Controller(new ClassPathResource(TRANSCRIPT_LOCATION));
    var challenge = new Challenge72(new ClassPathResource(TRANSCRIPT_LOCATION));

    assertThat(controller.codexTranscript().getBody()).contains(challenge.spoiler().solution());
  }

  @Test
  void shouldReturnServerErrorWhenTheTranscriptIsMissing() {
    var controller =
        new Challenge72Controller(new ClassPathResource("challenges/challenge-72/missing.md"));

    assertThat(controller.codexTranscript().getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
