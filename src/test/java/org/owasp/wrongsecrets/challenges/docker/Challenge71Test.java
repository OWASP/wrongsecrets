package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class Challenge71Test {

  private static final String SKILL_BUNDLE =
      "challenges/challenge-71/claude-skill/incident-reporter.skill";

  private static Resource skillBundleContaining(String scriptContent) {
    return buildSkillBundle(scriptContent);
  }

  private static Resource buildSkillBundle(String uploadScriptContent) {
    try {
      var baos = new java.io.ByteArrayOutputStream();
      try (var zip = new java.util.zip.ZipOutputStream(baos)) {
        var entry = new java.util.zip.ZipEntry("incident-reporter/scripts/upload_report.py");
        zip.putNextEntry(entry);
        zip.write(uploadScriptContent.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
      }
      return new ByteArrayResource(baos.toByteArray());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Resource uploaderWithToken(String token) {
    var encoded = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    return skillBundleContaining("UPLOAD_TOKEN_B64 = \"" + encoded + "\"\n");
  }

  @Test
  void spoilerShouldGiveTheDecodedTokenFromTheShippedSkill() {
    var challenge = new Challenge71(new ClassPathResource(SKILL_BUNDLE));

    assertThat(challenge.spoiler().solution()).isNotEmpty().isNotEqualTo(FILE_MOUNT_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void answerShouldNotBeTheEncodedValueThatIsInTheBundle() throws Exception {
    var skillResource = new ClassPathResource(SKILL_BUNDLE);
    var challenge = new Challenge71(skillResource);

    var zip =
        new java.util.zip.ZipInputStream(skillResource.getInputStream(), StandardCharsets.UTF_8);
    String scriptContent = null;
    for (var entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
      if (entry.getName().endsWith("upload_report.py")) {
        scriptContent = new String(zip.readAllBytes(), StandardCharsets.UTF_8);
        break;
      }
    }
    zip.close();

    assertThat(scriptContent).contains("UPLOAD_TOKEN_B64 = \"");
    assertThat(scriptContent).doesNotContain(challenge.spoiler().solution());
  }

  @Test
  void shouldDecodeTheTokenFromTheUploaderScript() {
    var challenge = new Challenge71(uploaderWithToken("t0k3n-from-the-bundle"));

    assertThat(challenge.spoiler().solution()).isEqualTo("t0k3n-from-the-bundle");
    assertThat(challenge.answerCorrect("t0k3n-from-the-bundle")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge71(new ClassPathResource(SKILL_BUNDLE));

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
  }

  @Test
  void shouldReportAnErrorWhenTheUploaderHasNoToken() {
    var challenge =
        new Challenge71(skillBundleContaining("TRACKER_URL = \"https://example.com\"\n"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void shouldReportAnErrorWhenTheTokenIsNotValidBase64() {
    var challenge =
        new Challenge71(skillBundleContaining("UPLOAD_TOKEN_B64 = \"not base64 %%\"\n"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void shouldReportAnErrorWhenTheUploaderCannotBeRead() {
    var challenge =
        new Challenge71(new ClassPathResource("challenges/challenge-71/does-not-exist.py"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }
}
