package com.example.secrettextprinter;

public interface Scoring {
    void completeChallenge(int challengeNumber);

    boolean getChallengeCompleted(int challengeNumber);

    float getProgress();

    int getTotalReceivedPoints();
}
