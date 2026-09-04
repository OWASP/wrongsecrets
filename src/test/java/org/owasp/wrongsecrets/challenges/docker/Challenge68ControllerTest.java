package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

class Challenge68ControllerTest {

  private static final String SKILL_MD = "incident-reporter/SKILL.md";
  private static final String UPLOADER = "incident-reporter/scripts/upload_report.py";
  private static final String RUNBOOK = "incident-reporter/references/runbook.md";

  private static Map<String, String> unzip(byte[] bundle) throws IOException {
    var entries = new HashMap<String, String>();
    try (var zip = new ZipInputStream(new ByteArrayInputStream(bundle))) {
      for (var entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
        entries.put(entry.getName(), new String(zip.readAllBytes(), StandardCharsets.UTF_8));
      }
    }
    return entries;
  }

  @Test
  void bundleShouldContainAllSkillFilesRelativeToTheSkillFolder() throws IOException {
    var entries = unzip(new Challenge68Controller().zipSkill());

    assertThat(entries).containsOnlyKeys(SKILL_MD, UPLOADER, RUNBOOK);
  }

  @Test
  void bundledSkillFileShouldNotContainTheSecret() throws IOException {
    var entries = unzip(new Challenge68Controller().zipSkill());

    assertThat(entries.get(SKILL_MD)).contains("name: incident-reporter");
    assertThat(entries.get(SKILL_MD)).doesNotContain("UPLOAD_TOKEN_B64");
  }

  @Test
  void bundledUploaderShouldCarryTheEncodedTokenAndNotThePlaintextOne() throws IOException {
    var entries = unzip(new Challenge68Controller().zipSkill());
    var challenge =
        new Challenge68(new ClassPathResource(Challenge68Controller.SKILL_ROOT + UPLOADER));

    assertThat(entries.get(UPLOADER)).contains("UPLOAD_TOKEN_B64 = \"");
    assertThat(entries.get(UPLOADER)).doesNotContain(challenge.spoiler().solution());
  }

  @Test
  void bundleShouldBeReproducible() throws IOException {
    assertThat(new Challenge68Controller().zipSkill())
        .isEqualTo(new Challenge68Controller().zipSkill());
  }

  @Test
  void shouldServeTheBundleAsAZipDownload() {
    var response = new Challenge68Controller().claudeSkillBundle();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).hasToString("application/zip");
    assertThat(response.getHeaders().getContentDisposition().getFilename())
        .isEqualTo("incident-reporter.zip");
    assertThat(response.getBody()).isNotEmpty();
  }
}
