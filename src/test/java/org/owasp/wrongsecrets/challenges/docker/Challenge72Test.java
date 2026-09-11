package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class Challenge72Test {

  private static final String TRANSCRIPT_LOCATION =
      "challenges/challenge-72/codex-session-transcript.md";

  private static Resource transcriptContaining(String content) {
    return new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void spoilerShouldGiveTheTokenFromTheShippedTranscript() {
    var challenge = new Challenge72(new ClassPathResource(TRANSCRIPT_LOCATION));

    assertThat(challenge.spoiler().solution()).isNotEmpty().isNotEqualTo(FILE_MOUNT_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void shippedTranscriptShouldContainTheToken() throws Exception {
    var transcript =
        new ClassPathResource(TRANSCRIPT_LOCATION).getContentAsString(StandardCharsets.UTF_8);

    assertThat(transcript).contains("DEPLOY_TOKEN=");
  }

  @Test
  void shouldExtractTheTokenFromTheTranscript() {
    var challenge =
        new Challenge72(
            transcriptContaining(
                """
                succeeded in 438ms:
                # Staging environment configuration
                # DO NOT COMMIT - shared team token

                API_BASE_URL=https://staging.internal.wrongsecrets.example.com
                DEPLOY_TOKEN=TestToken123
                LOG_LEVEL=debug
                """));

    assertThat(challenge.spoiler().solution()).isEqualTo("TestToken123");
    assertThat(challenge.answerCorrect("TestToken123")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge72(new ClassPathResource(TRANSCRIPT_LOCATION));

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
  }

  @Test
  void shouldReportAnErrorWhenTheTranscriptHasNoToken() {
    var challenge =
        new Challenge72(transcriptContaining("# Session transcript\n\nNo secrets here.\n"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void shouldReportAnErrorWhenTheTranscriptCannotBeRead() {
    var challenge =
        new Challenge72(new ClassPathResource("challenges/challenge-72/does-not-exist.md"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }
}
