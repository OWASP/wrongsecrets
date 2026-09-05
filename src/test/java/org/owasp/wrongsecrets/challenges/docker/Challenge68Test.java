package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class Challenge68Test {

  private static final String UPLOADER_LOCATION =
      "challenges/challenge-68/claude-skill/incident-reporter/scripts/upload_report.py";

  private static Resource uploaderContaining(String content) {
    return new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
  }

  private static Resource uploaderWithToken(String token) {
    var encoded = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    return uploaderContaining("UPLOAD_TOKEN_B64 = \"" + encoded + "\"\n");
  }

  @Test
  void spoilerShouldGiveTheDecodedTokenFromTheShippedSkill() {
    var challenge = new Challenge68(new ClassPathResource(UPLOADER_LOCATION));

    assertThat(challenge.spoiler().solution()).isNotEmpty().isNotEqualTo(FILE_MOUNT_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void answerShouldNotBeTheEncodedValueThatIsInTheBundle() throws Exception {
    var uploader =
        new ClassPathResource(UPLOADER_LOCATION).getContentAsString(StandardCharsets.UTF_8);
    var challenge = new Challenge68(new ClassPathResource(UPLOADER_LOCATION));

    assertThat(uploader).contains("UPLOAD_TOKEN_B64 = \"");
    assertThat(uploader).doesNotContain(challenge.spoiler().solution());
  }

  @Test
  void shouldDecodeTheTokenFromTheUploaderScript() {
    var challenge = new Challenge68(uploaderWithToken("t0k3n-from-the-bundle"));

    assertThat(challenge.spoiler().solution()).isEqualTo("t0k3n-from-the-bundle");
    assertThat(challenge.answerCorrect("t0k3n-from-the-bundle")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge68(new ClassPathResource(UPLOADER_LOCATION));

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
  }

  @Test
  void shouldReportAnErrorWhenTheUploaderHasNoToken() {
    var challenge = new Challenge68(uploaderContaining("TRACKER_URL = \"https://example.com\"\n"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void shouldReportAnErrorWhenTheTokenIsNotValidBase64() {
    var challenge = new Challenge68(uploaderContaining("UPLOAD_TOKEN_B64 = \"not base64 %%\"\n"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void shouldReportAnErrorWhenTheUploaderCannotBeRead() {
    var challenge =
        new Challenge68(new ClassPathResource("challenges/challenge-68/does-not-exist.py"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }
}
