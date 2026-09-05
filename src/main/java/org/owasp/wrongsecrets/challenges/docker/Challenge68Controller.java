package org.owasp.wrongsecrets.challenges.docker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hosts the Claude skill of challenge 68. The skill files live in the resource folder and are
 * zipped on request, so participants download the same kind of bundle that is passed around when a
 * skill is shared.
 */
@Slf4j
@RestController
public class Challenge68Controller {

  static final String SKILL_ROOT = "challenges/challenge-68/claude-skill/";
  private static final String BUNDLE_NAME = "incident-reporter.zip";
  private static final MediaType ZIP = new MediaType("application", "zip");
  // Fixed timestamp (2024-01-01T00:00:00Z) so the generated bundle is reproducible.
  private static final long FIXED_ENTRY_TIME = 1704067200000L;

  /** The files that make up the skill, named as they appear inside the bundle. */
  private static final List<String> SKILL_FILES =
      List.of(
          "incident-reporter/SKILL.md",
          "incident-reporter/references/runbook.md",
          "incident-reporter/scripts/upload_report.py");

  /** Returns the {@code incident-reporter} Claude skill as a downloadable zip bundle. */
  @GetMapping("/skills/claude/incident-reporter.zip")
  public ResponseEntity<byte[]> claudeSkillBundle() {
    try {
      return ResponseEntity.ok()
          .contentType(ZIP)
          .headers(
              headers ->
                  headers.setContentDisposition(
                      ContentDisposition.attachment().filename(BUNDLE_NAME).build()))
          .body(zipSkill());
    } catch (IOException e) {
      log.warn("Unable to package the Claude skill of challenge 68", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** Zips the skill files from the resource folder, keeping their paths inside the bundle. */
  byte[] zipSkill() throws IOException {
    var bundle = new ByteArrayOutputStream();
    try (var zip = new ZipOutputStream(bundle)) {
      for (String name : SKILL_FILES) {
        var entry = new ZipEntry(name);
        entry.setTime(FIXED_ENTRY_TIME);
        zip.putNextEntry(entry);
        try (var content = new ClassPathResource(SKILL_ROOT + name).getInputStream()) {
          content.transferTo(zip);
        }
        zip.closeEntry();
      }
    }
    return bundle.toByteArray();
  }
}
