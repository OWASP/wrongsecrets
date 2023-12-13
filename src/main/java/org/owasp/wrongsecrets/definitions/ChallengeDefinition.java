package org.owasp.wrongsecrets.definitions;

import static java.util.stream.Collectors.toMap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.definitions.Sources.ChallengeSource;

/**
 * We can define a challenge as follows:
 *
 * <pre>
 *       challenges:
 *     - name: Challenge 0
 *       sources:
 *         - class-name: "org.owasp.wrongsecrets.challenges.docker.Challenge0"
 *           hint: "explanation/challenge0_hint.adoc"
 *           explanation: "explanation/challenge0.adoc"
 *           reason: "explanation/challenge0_reason.adoc"
 *           environments: *all_envs
 *       difficulty: *easy
 *       category: *intro
 *       ctf:
 *         enabled: true
 * </pre>
 *
 * <p>During runtime a {@link ChallengeDefinition} is linked to a {@link Challenge} instance. Be
 * aware a challenge can have multiple sources. This is useful for having different implementations
 * for different environments. For example, we can have a challenge which is implemented for AWS and
 * Azure. This way you can define multiple challenge classes and the correct one is chosen during
 * runtime.
 */
public record ChallengeDefinition(
    ChallengeName name,
    List<ChallengeSource> sources,
    Difficulty difficulty,
    ChallengeCategory category,
    Ctf ctf,
    Sources environmentToSource,
    Sources.TextWithFileLocation missingEnvironment) {

  @SuppressFBWarnings(value = "IP_PARAMETER_IS_DEAD_BUT_OVERWRITTEN")
  public ChallengeDefinition {
    environmentToSource =
        new Sources(
            sources.stream().collect(toMap(s -> s.environments(), s -> s)).entrySet().stream()
                .flatMap(
                    entry -> entry.getKey().stream().map(env -> Map.entry(env, entry.getValue())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  public Sources challengeSources() {
    return environmentToSource;
  }

  public int difficulty(List<Difficulty> difficulties) {
    return difficulties.indexOf(difficulty) + 1;
  }

  public Optional<ChallengeSource> source(RuntimeEnvironment runtimeEnvironment) {
    return environmentToSource.source(runtimeEnvironment);
  }

  public Set<Environment> supportedEnvironments() {
    return environmentToSource().sources().keySet();
  }
}
