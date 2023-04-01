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
 * This challenge requires the participant to provide a hardcoded docker ENV var as password
 * This challenge can be run in CTF mode and is limited to using Docker as a runtime environment.
 */
@Component
@Order(3)
public class Challenge3 extends Challenge {

    private final String hardcodedEnvPassword;

    public Challenge3(ScoreCard scoreCard, @Value("${DOCKER_ENV_PASSWORD}") String hardcodedEnvPassword) {
        super(scoreCard);
        this.hardcodedEnvPassword = hardcodedEnvPassword;
    }

    /**
     * {@inheritDoc}
     *
     * This challenge can always run in CTF mode.
     */
    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spoiler spoiler() {
        return new Spoiler(hardcodedEnvPassword);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean answerCorrect(String answer) {
        return hardcodedEnvPassword.equals(answer);
    }

    /**
     * {@inheritDoc}
     */
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    /**
     * {@inheritDoc}
     * Difficulty: 1
     */
    @Override
    public int difficulty() {
        return 1;
    }

    /**
     * {@inheritDoc}
     * Technology is Docker for this challenge.
     */
    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.DOCKER.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }
}
