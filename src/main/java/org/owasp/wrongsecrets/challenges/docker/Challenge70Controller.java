package org.owasp.wrongsecrets.challenges.docker;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hosts the Claude skill of challenge 71. The exported skill bundle lives in the resource folder
 * and is served directly so participants download the same file that was exported from Claude.
 */
@Slf4j
@RestController
public class Challenge70Controller {

  static final String SKILL_ROOT = "challenges/challenge-70/claude-skill/";
  private static final String BUNDLE_NAME = "incident-reporter.zip";
  private static final MediaType ZIP = new MediaType("application", "zip");

  /** Returns the exported {@code incident-reporter} Claude skill as a downloadable zip bundle. */
  @GetMapping("/skills/claude/incident-reporter.zip")
  public ResponseEntity<byte[]> claudeSkillBundle() {
    try {
      var resource = new ClassPathResource(SKILL_ROOT + "incident-reporter.skill");
      return ResponseEntity.ok()
          .contentType(ZIP)
          .headers(
              headers ->
                  headers.setContentDisposition(
                      ContentDisposition.attachment().filename(BUNDLE_NAME).build()))
          .body(resource.getContentAsByteArray());
    } catch (IOException e) {
      log.warn("Unable to serve the Claude skill of challenge 71", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
