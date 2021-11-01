package com.example.secrettextprinter;

import java.util.Arrays;

public class InMemoryScoring implements Scoring {

    private int maxPoints = 0;
    private ChallengeEntry[] challenges;

    public InMemoryScoring(int numberOfChallenge) {
        challenges = new ChallengeEntry[numberOfChallenge];
        for (int i = 0; numberOfChallenge > i; i++) {
            challenges[i] = new ChallengeEntry(50);
            maxPoints += 50;

        }
    }

    @Override
    public void completeChallenge(int challengeNumber) {
        challenges[challengeNumber - 1].complete();
    }

    @Override
    public boolean getChallengeCompleted(int challengeNumber) {
        return challenges[challengeNumber - 1].isCompleted();
    }

    @Override
    public float getProgress() {
        return (100 / (float) maxPoints) * getTotalReceivedPoints();
        //return progresspercentage
    }

    @Override
    public int getTotalReceivedPoints() {
        final int[] totalscore = {0};
        Arrays.stream(challenges)
                .filter(ChallengeEntry::isCompleted)
                .forEach(challengeEntry -> totalscore[0] += challengeEntry.getScore());
        return totalscore[0];
    }

    class ChallengeEntry {
        private boolean completed;
        private final int score;

        public ChallengeEntry(int score) {
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
