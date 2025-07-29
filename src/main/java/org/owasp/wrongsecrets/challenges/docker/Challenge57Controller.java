package org.owasp.wrongsecrets.challenges.docker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for Challenge 57 to trigger database connection error. */
@Slf4j
@RestController
@RequiredArgsConstructor
public class Challenge57Controller {

  private final Challenge57 challenge;

  /**
   * Endpoint to trigger a database connection error that exposes connection string with credentials.
   * This simulates what happens when applications try to connect to unavailable databases.
   */
  @GetMapping("/error-demo/database-connection")
  public String triggerDatabaseError() {
    log.info("Attempting database connection for Challenge 57...");
    return challenge.simulateDatabaseConnectionError();
  }
}