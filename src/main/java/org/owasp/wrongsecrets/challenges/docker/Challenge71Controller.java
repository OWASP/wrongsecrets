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
 * Hosts the Codex session transcript of challenge 71 straight from the resource folder, so
 * participants can read the transcript the same way an analyst would when reviewing agent output.
 */
@Slf4j
@RestController
public class Challenge71Controller {

  private static final MediaType MARKDOWN =
      new MediaType("text", "markdown", StandardCharsets.UTF_8);

  private final Resource transcriptFile;

  public Challenge71Controller(
      @Value("classpath:challenges/challenge-71/codex-session-transcript.md")
          Resource transcriptFile) {
    this.transcriptFile = transcriptFile;
  }

  /** Returns the raw Codex session transcript for challenge 71. */
  @GetMapping("/challenges/challenge-71/codex-session-transcript.md")
  public ResponseEntity<String> codexTranscript() {
    try {
      return ResponseEntity.ok()
          .contentType(MARKDOWN)
          .body(transcriptFile.getContentAsString(StandardCharsets.UTF_8));
    } catch (IOException e) {
      log.warn("Unable to serve the Codex transcript of challenge 71", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
