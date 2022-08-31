package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(24)
public class Challenge24 extends Challenge {

    public Challenge24(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(getActualData());
    }

    @Override
    public boolean answerCorrect(String answer) {
        log.info("challenge 24, actualdata: {}, answer: {}", getActualData(), answer);
        return getActualData().equals(answer);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    @Override
    public int difficulty() {
        return 2;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.CRYPTOGRAPHY.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

    public String getActualData() {
        return "00010203 04050607 08090A0B 0C0D0E0F 10111213 14151617 18191A1B 1C1D1E1F 20212223 24252627 28292A2B 2C2D2E2F 30313233 34353637 38393A3B 3C3D3E3F";

    }
}
