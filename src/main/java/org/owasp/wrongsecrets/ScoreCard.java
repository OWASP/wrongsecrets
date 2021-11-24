package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.challenges.Challenge;

public interface ScoreCard {
    void completeChallenge(int challengeNumber);

    boolean getChallengeCompleted(int challengeNumber);

    //TODO refactor score card so it is based on id not on challenge numbers
    default boolean getChallengeCompleted(String challengeNumber) {
        return getChallengeCompleted(Integer.valueOf(challengeNumber.replace("Challenge", "")));
    }

    float getProgress();

    int getTotalReceivedPoints();
}
