package org.owasp.wrongsecrets;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.owasp.wrongsecrets.definitions.Environment;

@AllArgsConstructor
@Getter
public class MissingEnvironmentException extends RuntimeException {
  private final String currentRuntimeEnvironment;
  private final List<Environment> environments;
}
