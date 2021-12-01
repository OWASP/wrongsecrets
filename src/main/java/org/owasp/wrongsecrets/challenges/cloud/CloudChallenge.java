package org.owasp.wrongsecrets.challenges.cloud;

import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;

public abstract class CloudChallenge extends Challenge {

    private final RuntimeEnvironment runtimeEnvironment;

    protected CloudChallenge(ScoreCard scoreCard, RuntimeEnvironment runtimeEnvironment) {
        super(scoreCard);
        this.runtimeEnvironment = runtimeEnvironment;
    }

    public boolean isAWS() {
        return this.runtimeEnvironment.getRuntimeEnvironment() == RuntimeEnvironment.Environment.AWS;
    }

    public boolean isGCP() {
        return this.runtimeEnvironment.getRuntimeEnvironment() == RuntimeEnvironment.Environment.GCP;
    }

    @Override
    public String getExplanation() {
        return String.format("%s%s", super.getExplanation(), isGCP() ? "-gcp" : "");
    }
}
