package org.owasp.wrongsecrets;

import static java.util.stream.Collectors.toMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.owasp.wrongsecrets.definitions.ChallengeDefinition;
import org.owasp.wrongsecrets.definitions.Difficulty;

/** In-memory implementation of the ScoreCard (E.g. no persistence). */
public class InMemoryScoreCard implements ScoreCard {

  private final Set<ChallengeDefinition> solvedChallenges = new HashSet<>();
  private final Challenges challenges;
  private final Map<Difficulty, Integer> difficultyLevelsToInt;

  private int cachedTotalReceivedPoints = 0;

  public InMemoryScoreCard(Challenges challenges) {
    this.challenges = challenges;
    var difficulties = challenges.difficulties();
    this.difficultyLevelsToInt =
        IntStream.range(1, difficulties.size() + 1)
            .boxed()
            .collect(toMap(i -> difficulties.get(i - 1), i -> i));
  }

  @Override
  public void completeChallenge(ChallengeDefinition challengeDefinition) {
    if (solvedChallenges.add(challengeDefinition)) {
      cachedTotalReceivedPoints += calculatePoints(challengeDefinition);
    }
  }

  @Override
  public boolean getChallengeCompleted(ChallengeDefinition challenge) {
    return solvedChallenges.contains(challenge);
  }

  @Override
  public float getProgress() {
    return ((float) 100 / challenges.numberOfChallenges()) * solvedChallenges.size();
  }

  @Override
  public int getTotalReceivedPoints() {
    return cachedTotalReceivedPoints;
  }

  @Override
  public void reset(ChallengeDefinition challenge) {
    if (solvedChallenges.remove(challenge)) {
      cachedTotalReceivedPoints -= calculatePoints(challenge);
    }
  }

  private int calculatePoints(ChallengeDefinition challenge) {
    int level = difficultyLevelsToInt.get(challenge.difficulty());
    return level * (100 + (level - 1) * 25);
  }
}
