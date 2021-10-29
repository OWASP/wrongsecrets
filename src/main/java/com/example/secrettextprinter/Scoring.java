package com.example.secrettextprinter;

import java.util.Arrays;

public class Scoring {

    private int maxPoints = 0;
    private ChallengeEntry[] challenges;

    public Scoring(int numberOfChallenge) {
        challenges = new ChallengeEntry[numberOfChallenge];
        for (int i = 0; numberOfChallenge > i; i++) {
            challenges[i] = new ChallengeEntry(50);
            maxPoints += 50;

        }
    }

    public void completeChallenge(int challengeNumber) {
        challenges[challengeNumber - 1].complete();
    }

    public boolean getChallengeCompleted(int challengeNumber) {
        return challenges[challengeNumber - 1].isCompleted();
    }

    public float getProgress() {
        return (100 / (float) maxPoints) * getTotalReceivedPoints();
        //return progresspercentage
    }

    public int getTotalReceivedPoints() {
        final int[] totalscore = {0};
        Arrays.stream(challenges).filter(challengeEntry -> challengeEntry.isCompleted()).forEach(challengeEntry -> totalscore[0] += challengeEntry.getScore());
        return totalscore[0];
    }

    class ChallengeEntry {
        private boolean completed;
        private int score;

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
