package org.owasp.wrongsecrets.definitions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.owasp.wrongsecrets.ChallengeUiTemplateResolver;
import org.owasp.wrongsecrets.RuntimeEnvironment;

/**
 * We allow to define multiple sources for a challenge. This is useful for having different
 * implementations for different environments. For example, we can have a challenge which is
 * implemented for AWS and Azure. This way you can define multiple challenge classes and the correct
 * one is chosen based on the runtime environment. The runtime environment is defined as environment
 * variable or system property or in application.properties.
 */
public record Sources(Map<Environment, ChallengeSource> sources) {
  public Optional<ChallengeSource> source(RuntimeEnvironment runtimeEnvironment) {
    return Optional.ofNullable(sources.get(runtimeEnvironment.getRuntimeEnvironment()));
  }

  /**
   * Represent a single source for a challenge.
   *
   * @param className the name of the class which implements the challenge
   * @param environments the environments for which this challenge should run on
   * @param explanation the filename of the explanation
   * @param reason the filename of the reason
   * @param hint the filename of the hint
   * @param hintLimited the filename of the hint when the user has limited access
   * @param uiSnippet the filename of the UI snippet, this can be used to include JavaScript and is
   *     used by {@link ChallengeUiTemplateResolver}
   */
  public record ChallengeSource(
      String className,
      List<Environment> environments,
      TextWithFileLocation explanation,
      TextWithFileLocation reason,
      TextWithFileLocation hint,
      TextWithFileLocation hintLimited,
      String uiSnippet) {}

  public record TextWithFileLocation(String fileName, Supplier<String> contents) {}
}
