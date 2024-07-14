package org.owasp.wrongsecrets;

import java.util.stream.Collectors;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class StartupFailureAnalyzer extends AbstractFailureAnalyzer<MissingEnvironmentException> {

  @Override
  protected FailureAnalysis analyze(Throwable rootFailure, MissingEnvironmentException cause) {
    return new FailureAnalysis(getDescription(cause), getAction(cause), cause);
  }

  private String getDescription(MissingEnvironmentException ex) {
    return String.format(
        "K8S_ENV is set to: '%s' which is not correct", ex.getCurrentRuntimeEnvironment());
  }

  private String getAction(MissingEnvironmentException ex) {
    return String.format(
        "Consider updating the K8S_ENV environment variable to one of the expected values '%s'",
        ex.getEnvironments().stream().map(e -> e.name()).collect(Collectors.joining(", ")));
  }
}
