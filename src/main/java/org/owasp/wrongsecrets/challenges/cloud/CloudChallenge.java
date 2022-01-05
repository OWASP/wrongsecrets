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

    public boolean isAzure() {
        return this.runtimeEnvironment.getRuntimeEnvironment() == RuntimeEnvironment.Environment.AZURE;
    }

    @Override
    public String getExplanation() {
        RuntimeEnvironment.Environment env = runtimeEnvironment.getRuntimeEnvironment();
        switch (env) {
            case GCP:
                return String.format("%s%s", super.getExplanation(), "-gcp");
            case AZURE:
                return String.format("%s%s", super.getExplanation(), "-azure");
            default:
                return String.format("%s", super.getExplanation()); // Default is AWS
        }
    }
}
