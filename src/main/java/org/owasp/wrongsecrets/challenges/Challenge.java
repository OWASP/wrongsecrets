package org.owasp.wrongsecrets.challenges;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.owasp.wrongsecrets.RuntimeEnvironment.Environment;
import org.owasp.wrongsecrets.ScoreCard;

/**
 * General Abstract Challenge class containing all the necessary members for a challenge.
 *
 * @see org.owasp.wrongsecrets.ScoreCard for tracking
 */
@RequiredArgsConstructor
@Getter
public abstract class Challenge {

  private final ScoreCard scoreCard;

  /**
   * Returns a Spoiler object containing the secret for the challenge.
   *
   * @return Spoiler with anser
   */
  public abstract Spoiler spoiler();

  /**
   * method that needs to be overwritten by the Challenge implementation class to do the actual
   * evaluation of the answer.
   *
   * @param answer String provided by the user
   * @return true if answer is Correct
   */
  protected abstract boolean answerCorrect(String answer);

  /**
   * Gives the supported runtime envs in which the class can run.
   *
   * @return a list of Environment objects representing supported envs for the class
   */
  public abstract List<Environment> supportedRuntimeEnvironments();

  /**
   * returns the difficulty level.
   *
   * @return int with difficulty
   */
  public abstract int difficulty();

  /**
   * returns the technology used.
   *
   * @see ChallengeTechnology.Tech
   * @return a string from Tech.id
   */
  public abstract String getTech();

  /**
   * boolean indicating a challenge needs to be run differently with a different explanation/steps
   * when running on a shared platform.
   *
   * @return boolean with true if a different explanation is required when running on a shared
   *     platform
   */
  public abstract boolean isLimitedWhenOnlineHosted();

  /**
   * boolean indicating if the challenge can be enabled when running in CTF mode. Note: All
   * challenges should be able to run in non-CTF mode.
   *
   * @return true if the challenge can be run in CTF mode.
   */
  public abstract boolean canRunInCTFMode();

  /**
   * Solving method which, if the correct answer is provided, will mark the challenge as solved in
   * the scorecard.
   *
   * @param answer String provided by the user to validate.
   * @return true if answer was correct.
   */
  public boolean solved(String answer) {
    var correctAnswer = answerCorrect(answer);
    if (correctAnswer) {
      scoreCard.completeChallenge(this);
    }
    return correctAnswer;
  }

  /**
   * Returns the name of the explanation file for adoc rendering.
   *
   * @return String with name of file for explanation
   */
  public String getExplanation() {
    return this.getClass().getSimpleName().toLowerCase();
  }

  /**
   * Returns the name of the hints file for adoc rendering.
   *
   * @return String with name of file for hints
   */
  public String getHint() {
    return this.getClass().getSimpleName().toLowerCase() + "_hint";
  }

  /**
   * Returns the name of the reason file for adoc rendering.
   *
   * @return String with name of file for reason
   */
  public String getReason() {
    return this.getClass().getSimpleName().toLowerCase() + "_reason";
  }

  /**
   * Returns the number of the challenge extracted from the classname
   *
   * @return int of the challenge
   */
  public String getNumber() {
    return this.getClass().getSimpleName().replaceAll("[^0-9]", "");
  }
}
