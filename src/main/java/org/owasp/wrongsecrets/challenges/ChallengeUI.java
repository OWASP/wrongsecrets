package org.owasp.wrongsecrets.challenges;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.definitions.ChallengeDefinition;
import org.owasp.wrongsecrets.definitions.Difficulty;
import org.owasp.wrongsecrets.definitions.Environment;
import org.owasp.wrongsecrets.definitions.Navigator;
import org.owasp.wrongsecrets.definitions.Sources.ChallengeSource;

/** Wrapper class to move logic from Thymeleaf to keep logic in code instead of the html file. */
@Getter
public class ChallengeUI {

  private final DifficultyUI difficultyUI;
  private final List<Environment> environments;
  private final Navigator navigation;

  /** Wrapper class to express the difficulty level into a UI representation. */
  private record DifficultyUI(int difficulty, int totalOfDifficultyLevels) {

    public String minimal() {
      return "☆".repeat(difficulty);
    }

    public String scale() {
      String fullScale = "★".repeat(difficulty) + "☆".repeat(totalOfDifficultyLevels);
      return fullScale.substring(0, totalOfDifficultyLevels);
    }
  }

  private final ChallengeDefinition challengeDefinition;
  private final ScoreCard scoreCard;
  private final RuntimeEnvironment runtimeEnvironment;
  private final List<Difficulty> difficulties;

  public ChallengeUI(
      ChallengeDefinition challengeDefinition,
      ScoreCard scoreCard,
      RuntimeEnvironment runtimeEnvironment,
      List<Difficulty> difficulties,
      List<Environment> environments,
      Navigator navigation) {
    this.challengeDefinition = challengeDefinition;
    this.scoreCard = scoreCard;
    this.runtimeEnvironment = runtimeEnvironment;
    this.difficulties = difficulties;
    this.environments = environments;
    this.navigation = navigation;
    this.difficultyUI =
        new DifficultyUI(challengeDefinition.difficulty(difficulties), difficulties.size());
  }

  /**
   * Converts the name of the class into the challenge name.
   *
   * @return String with name of the challenge.
   */
  public String getName() {
    return challengeDefinition.name().name();
  }

  /**
   * gives back the number of the challenge.
   *
   * @return the html friendly shortName name for the challenge
   */
  public String getLink() {
    return challengeDefinition.name().shortName();
  }

  /**
   * Returns the tech used for a challenge.
   *
   * @return string with tech.
   */
  public String getTech() {
    return challengeDefinition.category().category();
  }

  /**
   * Returns the number of the next challenge (e.g current+1).
   *
   * @return int with next challenge number.
   */
  public String next() {
    return navigation.next().map(c -> c.name().shortName()).orElse(null);
  }

  /**
   * Returns the number of the previous challenge.
   *
   * @return int with previous challenge number.
   */
  public String previous() {
    return navigation.previous().map(c -> c.name().shortName()).orElse(null);
  }

  private String documentation(Function<ChallengeSource, String> extractor) {
    if (runtimeEnvironment.canRun(challengeDefinition)) {
      return challengeDefinition.source(runtimeEnvironment).map(extractor).orElse("");
    } else {
      // We cannot run the challenge but showing documentation should still be possible
      return extractor.apply(challengeDefinition.sources().getFirst());
    }
  }

  /**
   * Returns filename of the explanation of the challenge.
   *
   * @return String with filename.
   */
  public String getExplanation() {
    return documentation(s -> s.explanation().fileName());
  }

  /**
   * Returns filename of the hints for the challenge.
   *
   * @return String with filename.
   */
  public String getHint() {
    return documentation(s -> s.hint().fileName());
  }

  /**
   * Returns filename of the reasons of the challenge.
   *
   * @return String with filename.
   */
  public String getReason() {
    return documentation(s -> s.reason().fileName());
  }

  /**
   * String providing the minimal required env. Used in homescreen.
   *
   * @return String with required env.
   */
  public String requiredEnv() {
    return challengeDefinition.supportedEnvironments().stream()
        .map(e -> e.name())
        .limit(1)
        .collect(Collectors.joining())
        .toUpperCase();
  }

  /**
   * Returns the difficulty level in stars on a full scale, for example for level NORMAL it will
   * return "★★☆☆☆".
   *
   * @return stars
   */
  public String getStarsOnScale() {
    return difficultyUI.scale();
  }

  /**
   * Used to setup the label for the link to the challenge on the homescreen return "challenge
   * 1(_disabled)(_solveD)-link".
   *
   * @return label
   */
  public String getDataLabel() {
    String label = challengeDefinition.name().shortName().trim().toLowerCase();
    if (!this.isChallengeEnabled()) {
      label = label + "_disabled";
    }
    if (challengeCompleted()) {
      label = label + "_completed";
    }
    label = label + "-link";
    return label;
  }

  /**
   * Used to return whether the challenge is completed or not.
   *
   * @return boolean
   */
  public boolean challengeCompleted() {
    if (!runtimeEnvironment.runtimeInCTFMode()) {
      return scoreCard.getChallengeCompleted(challengeDefinition);
    }
    return false;
  }

  /**
   * Returns the difficulty level in stars, for example for level NORMAL it will return "☆☆".
   *
   * @return stars
   */
  public String getStars() {
    return difficultyUI.minimal();
  }

  /**
   * checks whether challenge is enabled based on used runtimemode and CTF enablement.
   *
   * @return boolean true if the challenge can run.
   */
  public boolean isChallengeEnabled() {
    if (runtimeEnvironment.runtimeInCTFMode()) {
      return runtimeEnvironment.canRun(challengeDefinition) && challengeDefinition.ctf().enabled();
    }
    return runtimeEnvironment.canRun(challengeDefinition);
  }

  public String getUiSnippet() {
    return challengeDefinition
        .source(runtimeEnvironment)
        .map(source -> source.uiSnippet())
        .orElse("");
  }

  public String getRuntimeEnvironmentCategory() {
    var challengeEnvironmentsOverviewNames =
        challengeDefinition.supportedEnvironments().stream().map(env -> env.overview()).toList();
    if (challengeEnvironmentsOverviewNames.size() == 1) {
      return challengeEnvironmentsOverviewNames.iterator().next();
    } else {
      // Now if we have multiple we select the lowest one for our global environment definition
      var allEnvironmentsOverviewNames = environments.stream().map(env -> env.overview()).toList();
      return allEnvironmentsOverviewNames.stream()
          .filter(name -> challengeEnvironmentsOverviewNames.contains(name))
          .findFirst()
          .orElse("Unknown");
    }
  }

  public static ChallengeUI toUI(
      ChallengeDefinition definition,
      ScoreCard scoreCard,
      RuntimeEnvironment environment,
      List<Difficulty> difficulties,
      List<Environment> environments,
      Navigator navigation) {
    return new ChallengeUI(
        definition, scoreCard, environment, difficulties, environments, navigation);
  }
}
