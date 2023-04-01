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

    protected abstract boolean answerCorrect(String answer) throws Exception;

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

    /**
     * boolean indicating a challenge needs to be run differently with a different explanation/steps when running on a shared platform
     * @return boolean with true if a different explanation is required when running on a shared platform
     */
    public abstract boolean isLimittedWhenOnlineHosted();

    /**
     * boolean indicating if the challenge can be enabled when running in CTF mode.
     * Note: All challenges should be able to run in non-CTF mode.
     * @return true if th echallenge can be run in CTF mode.
     */
    public abstract boolean canRunInCTFMode();

    /**
     * Solving method which, if the correct answer is provided, will mark the challenge as solved in the scorecard
     * @param answer String provided by the user to validate.
     * @return true if answer was correct.
     */
    public boolean solved(String answer) {
        boolean correctAnswer = false;
        try {
            correctAnswer = answerCorrect(answer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (correctAnswer) {
            scoreCard.completeChallenge(this);
        }
        return correctAnswer;
    }

    /**
     * Returns the name of the explanation file for adoc rendering
     * @return String with name of file for explanation
     */
    public String getExplanation() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    /**
     * Returns the name of the hints file for adoc rendering
     * @return String with name of file for hints
     */
    public String getHint() {
        return this.getClass().getSimpleName().toLowerCase() + "_hint";
    }

    /**
     * Returns the name of the reason file for adoc rendering
     * @return String with name of file for reason
     */
    public String getReason() {
        return this.getClass().getSimpleName().toLowerCase() + "_reason";
    }
}
