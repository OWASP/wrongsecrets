package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.challenges.Challenge;

public interface ScoreCard {
    void completeChallenge(int challengeNumber);

    boolean getChallengeCompleted(int challengeNumber);

    float getProgress();

    int getTotalReceivedPoints();
}
