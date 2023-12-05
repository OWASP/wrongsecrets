package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.AuditConfiguration;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This is a challenge based on finding API Key in Spring Boot Actuator audit events
 */
@Slf4j
@Component
@Order(42)
public class Challenge42 extends Challenge {

    private final AuditConfiguration auditConfiguration;

    public Challenge42(ScoreCard scoreCard, AuditConfiguration auditConfiguration) {
        super(scoreCard);
        this.auditConfiguration = auditConfiguration;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(auditConfiguration.getApiKey());
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return auditConfiguration.getApiKey().equals(answer);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    @Override
    public int difficulty() {
        return Difficulty.EASY;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.LOGGING.id;
    }

    @Override
    public boolean isLimitedWhenOnlineHosted() {
        return false;
    }

    @Override
    public boolean canRunInCTFMode() {
        return false;
    }
}
