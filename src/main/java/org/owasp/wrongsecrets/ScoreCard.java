package org.owasp.wrongsecrets;

public interface ScoreCard {
    void completeChallenge(int challengeNumber);

    boolean getChallengeCompleted(int challengeNumber);

    float getProgress();

    int getTotalReceivedPoints();
}
