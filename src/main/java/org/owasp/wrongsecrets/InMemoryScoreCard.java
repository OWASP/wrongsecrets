package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.challenges.Challenge;

import java.util.HashSet;
import java.util.Set;

public class InMemoryScoreCard implements ScoreCard {

    private final int maxPoints;
    private final Set<Challenge> solvedChallenges = new HashSet<>();

    public InMemoryScoreCard(int numberOfChallenge) {
        maxPoints = numberOfChallenge * 50;
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
        return (100 / (float) maxPoints) * getTotalReceivedPoints();
    }

    @Override
    public int getTotalReceivedPoints() {
        return solvedChallenges.size() * 50;
    }
}
