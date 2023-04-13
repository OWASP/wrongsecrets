package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

/**
 * Introduction challenge to get a user introduced with the setup.
 */
@Component
@Order(0)
public class Challenge0 extends Challenge {

    public Challenge0(ScoreCard scoreCard) {
        super(scoreCard);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spoiler spoiler() {
        return new Spoiler(getData());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean answerCorrect(String answer) {
        return getData().equals(answer);
    }

    @Override
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

    @Override
    public String getTech() {
        return "Intro";
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    private String getData() {
        return "The first answer";
    }
}
