package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Order(37)
public class Challenge37 extends Challenge {

    private String secret;

    public Challenge37(ScoreCard scoreCard) {
        super(scoreCard);
        secret = UUID.randomUUID().toString();
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(secret);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return secret.equals(answer);
    }

    @Override
    public int difficulty() {
        return Difficulty.NORMAL;
    }

    /** {@inheritDoc} This is a CICD type of challenge */
    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.CICD.id;
    }

    @Override
    public boolean isLimitedWhenOnlineHosted() {
        return false;
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }
}
