package org.owasp.wrongsecrets.challenges.docker;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the SQLite user database of challenge 76 straight from the resource folder, so
 * participants can download it and crack the stored password hashes and ciphertexts offline.
 */
@Slf4j
@RestController
public class Challenge76Controller {

  private final Resource databaseFile;

  /**
   * Constructor for creating a new Challenge76Controller object.
   *
   * @param databaseFile the SQLite database containing the synthetic user records.
   */
  public Challenge76Controller(
      @Value("classpath:challenges/challenge-76/wrongsecrets-users.sqlite") Resource databaseFile) {
    this.databaseFile = databaseFile;
  }

  /** Returns the SQLite user database of challenge 76. */
  @GetMapping("/challenges/challenge-76/wrongsecrets-users.sqlite")
  public ResponseEntity<byte[]> userDatabase() {
    try {
      var databaseContent = databaseFile.getContentAsByteArray();
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(databaseContent);
    } catch (IOException e) {
      log.warn("Unable to serve the user database of challenge 76", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
