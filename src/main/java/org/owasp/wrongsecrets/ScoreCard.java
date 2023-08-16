package org.owasp.wrongsecrets;

import java.util.List;
import org.owasp.wrongsecrets.challenges.Challenge;

/** Interface of a scorecard where a player's progress is stored into. */
public interface ScoreCard {

  /**
   * Marks a challenge as completed.
   *
   * @param challenge Challenge object which is completed
   */
  void completeChallenge(Challenge challenge);

  /**
   * Checks if the given challenge is marked as completed in the scorecard.
   *
   * @param challenge Challenge object tested for completion
   * @return true if challenge solved correctly
   */
  boolean getChallengeCompleted(Challenge challenge);

  /**
   * Gives a 0-100 implementation completeness score.
   *
   * @return float with completeness percentage
   */
  float getProgress();

  /**
   * Gives total number of received points.
   *
   * @return int with points
   */
  int getTotalReceivedPoints();

  /**
   * Gives all completed challenges
   *
   * @return Set of ints
   */
  List<String> getCompletedChallenges();

  /**
   * Resets the status of a given challenge its entry in the score-card.
   *
   * @param challenge Challenge of which the status should be reset.
   */
  void reset(Challenge challenge);
}
