package org.owasp.wrongsecrets;

import org.springframework.boot.ExitCodeGenerator;

/** Used to give a clear non-0 exit code when the Application cannot start. */
public class FailtoStartupException extends RuntimeException implements ExitCodeGenerator {

  public FailtoStartupException(String message) {
    super(message);
  }

  @Override
  public int getExitCode() {
    return 1;
  }
}
