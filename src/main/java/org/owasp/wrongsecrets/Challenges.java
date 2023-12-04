package org.owasp.wrongsecrets;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.definitions.ChallengeDefinition;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.owasp.wrongsecrets.definitions.Difficulty;
import org.owasp.wrongsecrets.definitions.Navigator;

/**
 * A collection of all challenges. This class glues a challenge definition together with the
 * challenge class.
 */
public class Challenges {

  @Getter private final ChallengeDefinitionsConfiguration definitions;
  private final Map<String, ChallengeDefinition> shortNameToDefinition;
  private final Map<String, Challenge> classNameToChallenge;
  private final Map<ChallengeDefinition, List<Challenge>> challengeDefinitionToChallenge;

  public Challenges(ChallengeDefinitionsConfiguration definitions, List<Challenge> challenges) {
    this.definitions = definitions;

    shortNameToDefinition =
        definitions.challenges().stream()
            .collect(toMap(definition -> definition.name().shortName(), Function.identity()));
    classNameToChallenge =
        challenges.stream()
            .collect(toMap(challenge -> challenge.getClass().getName(), Function.identity()));
    challengeDefinitionToChallenge =
        definitions.challenges().stream()
            .collect(
                toMap(
                    definition -> definition,
                    definition ->
                        definition.sources().stream()
                            .map(source -> classNameToChallenge.get(source.className()))
                            .toList()));
  }

  public Navigator navigation(ChallengeDefinition challengeDefinition) {
    return new Navigator(definitions.challenges(), challengeDefinition);
  }

  public int numberOfChallenges() {
    return definitions.challenges().size();
  }

  public Optional<ChallengeDefinition> findByShortName(String shortName) {
    return definitions.challenges().stream()
        .filter(challenge -> challenge.name().shortName().equals(shortName))
        .findFirst();
  }

  /**
   * Find a challenge based on the name and the runtime environment.
   *
   * @param shortChallengeName the name of the challenge
   * @param runtimeEnvironment the runtime environment
   * @return the challenge if found
   */
  public Optional<Challenge> findChallenge(
      String shortChallengeName, RuntimeEnvironment runtimeEnvironment) {
    var challengeDefinition = shortNameToDefinition.get(shortChallengeName);

    if (challengeDefinition == null) {
      return Optional.empty();
    }
    var source = challengeDefinition.source(runtimeEnvironment);
    return source.map(s -> classNameToChallenge.get(s.className()));
  }

  public List<Challenge> getChallenge(ChallengeDefinition definition) {
    return challengeDefinitionToChallenge.get(definition);
  }

  public List<Difficulty> difficulties() {
    return definitions.difficulties();
  }

  public boolean isFirstChallenge(ChallengeDefinition challengeDefinition) {
    return challengeDefinition.equals(definitions.challenges().get(0));
  }

  public List<ChallengeDefinition> getChallengeDefinitions() {
    return definitions.challenges();
  }
}
