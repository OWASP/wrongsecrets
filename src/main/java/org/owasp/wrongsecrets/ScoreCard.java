package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.challenges.Challenge;

public interface ScoreCard {
    void completeChallenge(Challenge challenge);

    boolean getChallengeCompleted(Challenge challenge);

    float getProgress();

    int getTotalReceivedPoints();
}
