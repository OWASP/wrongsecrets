package org.owasp.wrongsecrets.challenges.docker;


import java.nio.charset.StandardCharsets;

import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(28)
public class Challenge28 extends Challenge {

    private String secretKey = new String(Base64.decode(new String(Base64.decode("WVhOa1ptUndkVmxWU1dGa1ltRnZZWE5rY0dFd04ydHFNakF3TXc9PQ=="), UTF_8)), UTF_8);

    public Challenge28(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(secretKey);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return secretKey.equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    @Override
    public int difficulty() {
        return 1;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.DOCUMENTATION.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

}
