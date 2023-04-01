package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

/**
 * This challenge requires the participant to provide a hardcoded password to pass the challenge.
 * This challenge can be run in CTF mode and is limited to using Docker as a runtime environment.
 */
@Component
@Order(2)
public class Challenge2 extends Challenge {

    private final String hardcodedPassword;

    /**
     * Constructor for creating a new Challenge2 object.
     *
     * @param scoreCard         The scorecard object used for tracking points.
     * @param hardcodedPassword The hardcoded password for the challenge.
     */
    public Challenge2(ScoreCard scoreCard, @Value("${password}") String hardcodedPassword) {
        super(scoreCard);
        this.hardcodedPassword = hardcodedPassword;
    }

    /**
     * {@inheritDoc}
     * This challenge can always run in CTF mode.
     */
    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    /**
     * {@inheritDoc}
     * Returns a Spoiler object containing the hardcoded password for the challenge.
     */
    @Override
    public Spoiler spoiler() {
        return new Spoiler(hardcodedPassword);
    }

    /**
     * {@inheritDoc}
     * Checks if the provided answer matches the hardcoded password for the challenge.
     *
     * @param answer The answer provided by the participant.
     * @return True if the answer matches the hardcoded password, false otherwise.
     */
    @Override
    public boolean answerCorrect(String answer) {
        return hardcodedPassword.equals(answer);
    }

    /**
     * Returns a list of supported runtime environments for the challenge.
     * This challenge supports the Docker runtime environment.
     *
     * @return A list of supported runtime environments.
     */
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    /**
     * {@inheritDoc}
     * Returns the difficulty level of the challenge as 1.
     */
    @Override
    public int difficulty() {
        return 1;
    }

    /**
     * {@inheritDoc}
     * Returns the technology used for the challenge as GIT.
     */
    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.GIT.id;
    }

    /**
     * {@inheritDoc}
     * This challenge is not limited when hosted online.
     */
    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

}
