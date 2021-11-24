package com.example.secrettextprinter;

public interface ScoreCard {
    void completeChallenge(int challengeNumber);

    boolean getChallengeCompleted(int challengeNumber);

    float getProgress();

    int getTotalReceivedPoints();
}
