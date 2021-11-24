package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.challenges.Challenge;

import java.util.List;
import java.util.stream.IntStream;

public class InMemoryScoreCard implements ScoreCard {

    private final int maxPoints;
    private final List<ChallengeScore> challengeScores;

    public InMemoryScoreCard(int numberOfChallenge) {
        maxPoints = numberOfChallenge * 50;
        this.challengeScores = IntStream.range(0, numberOfChallenge)
                .mapToObj(i -> new ChallengeScore(50))
                .toList();
    }

    @Override
    public void completeChallenge(int challengeNumber) {
        challengeScores.get(challengeNumber - 1).complete();
    }

    @Override
    public boolean getChallengeCompleted(int challengeNumber) {
        return challengeScores.get(challengeNumber - 1).isCompleted();
    }

    @Override
    public float getProgress() {
        return (100 / (float) maxPoints) * getTotalReceivedPoints();
    }

    @Override
    public int getTotalReceivedPoints() {
        return challengeScores.stream()
                .filter(ChallengeScore::isCompleted)
                .mapToInt(ChallengeScore::getScore).sum();
    }

    class ChallengeScore {
        private boolean completed;
        private final int score;

        public ChallengeScore(int score) {
            this.score = score;
            completed = false;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void complete() {
            this.completed = true;
        }

        public int getScore() {
            return score;
        }
    }

}
