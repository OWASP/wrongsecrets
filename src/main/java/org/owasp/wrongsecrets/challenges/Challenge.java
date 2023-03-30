package org.owasp.wrongsecrets.challenges;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.owasp.wrongsecrets.RuntimeEnvironment.Environment;
import org.owasp.wrongsecrets.ScoreCard;

import java.util.List;

/**
 * General Abstract Challenge class containing all the necessary members for a challenge.
 * @see org.owasp.wrongsecrets.ScoreCard for tracking
 */
@RequiredArgsConstructor
@Getter
public abstract class Challenge {

    private final ScoreCard scoreCard;

    public abstract Spoiler spoiler();

    protected abstract boolean answerCorrect(String answer);

    /**
     * Gives the supported runtine envs in which the class can run
     * @return a list of Environment objects representing supported envs for the class
     */
    public abstract List<Environment> supportedRuntimeEnvironments();

    /**
     * returns the difficulty (1-5)
     * @return int with difficulty
     */
    public abstract int difficulty();

    /**
     * returns the technology used
     * @see ChallengeTechnology.Tech
     * @return a string from Tech.id
     */
    public abstract String getTech();

    public abstract boolean isLimittedWhenOnlineHosted();

    public abstract boolean canRunInCTFMode();

    public boolean solved(String answer) {
        var correctAnswer = answerCorrect(answer);
        if (correctAnswer) {
            scoreCard.completeChallenge(this);
        }
        return correctAnswer;
    }

    public String getExplanation() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public String getHint() {
        return this.getClass().getSimpleName().toLowerCase() + "_hint";
    }

    public String getReason() {
        return this.getClass().getSimpleName().toLowerCase() + "_reason";
    }
}
