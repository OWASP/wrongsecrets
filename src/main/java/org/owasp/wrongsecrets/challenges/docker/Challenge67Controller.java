package org.owasp.wrongsecrets.challenges.docker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hosts the Cursor skill of challenge 67 straight from the resource folder, so participants can
 * read the skill the same way an agent would.
 */
@Slf4j
@RestController
public class Challenge67Controller {

  private static final MediaType MARKDOWN =
      new MediaType("text", "markdown", StandardCharsets.UTF_8);

  private final Resource skillFile;

  public Challenge67Controller(
      @Value("classpath:challenges/challenge-67/cursor-skill/deploy-preview/SKILL.md")
          Resource skillFile) {
    this.skillFile = skillFile;
  }

  /** Returns the raw {@code SKILL.md} of the {@code deploy-preview} Cursor skill. */
  @GetMapping("/skills/cursor/deploy-preview/SKILL.md")
  public ResponseEntity<String> cursorSkill() {
    try {
      return ResponseEntity.ok()
          .contentType(MARKDOWN)
          .body(skillFile.getContentAsString(StandardCharsets.UTF_8));
    } catch (IOException e) {
      log.warn("Unable to serve the Cursor skill of challenge 67", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
