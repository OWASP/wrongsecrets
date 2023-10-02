package org.owasp.wrongsecrets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.owasp.wrongsecrets.challenges.Challenge;

/** In-memory implementation of the ScoreCard (E.g. no persistence). */
public class InMemoryScoreCard implements ScoreCard {

  private final int maxNumberOfChallenges;
  private final Set<Challenge> solvedChallenges = new HashSet<>();

  public InMemoryScoreCard(int numberOfChallenge) {
    maxNumberOfChallenges = numberOfChallenge;
  }

  @Override
  public void completeChallenge(Challenge challenge) {
    solvedChallenges.add(challenge);
  }

  @Override
  public boolean getChallengeCompleted(Challenge challenge) {
    return solvedChallenges.contains(challenge);
  }

  @Override
  public float getProgress() {
    return ((float) 100 / maxNumberOfChallenges) * solvedChallenges.size();
  }

  @Override
  public int getTotalReceivedPoints() {
    return solvedChallenges.stream()
        .map(challenge -> challenge.difficulty() * (100 + (challenge.difficulty() - 1) * 25))
        .reduce(0, Integer::sum);
  }

  @Override
  public List<String> getCompletedChallenges() {
    return solvedChallenges.stream().map(Challenge::getNumber).toList();
  }

  @Override
  public void reset(Challenge challenge) {
    solvedChallenges.remove(challenge);
  }
}
