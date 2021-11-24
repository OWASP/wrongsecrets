package com.example.secrettextprinter;

import java.util.List;
import java.util.stream.IntStream;

public class InMemoryScoreCard implements ScoreCard {

    private final int maxPoints;
    private final List<Challenge> challenges;

    public InMemoryScoreCard(int numberOfChallenge) {
        maxPoints = numberOfChallenge * 50;
        this.challenges = IntStream.range(0, numberOfChallenge)
                .mapToObj(i -> new Challenge(50))
                .toList();
    }

    @Override
    public void completeChallenge(int challengeNumber) {
        challenges.get(challengeNumber - 1).complete();
    }

    @Override
    public boolean getChallengeCompleted(int challengeNumber) {
        return challenges.get(challengeNumber - 1).isCompleted();
    }

    @Override
    public float getProgress() {
        return (100 / (float) maxPoints) * getTotalReceivedPoints();
    }

    @Override
    public int getTotalReceivedPoints() {
        return challenges.stream()
                .filter(Challenge::isCompleted)
                .mapToInt(Challenge::getScore).sum();
    }

    class Challenge {
        private boolean completed;
        private final int score;

        public Challenge(int score) {
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
