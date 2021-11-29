package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(1)
public class Challenge1 extends Challenge {

    private final String hardcodedPassword;

    public Challenge1(ScoreCard scoreCard, @Value("${password}") String hardcodedPassword) {
        super(scoreCard);
        this.hardcodedPassword = hardcodedPassword;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(hardcodedPassword);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return hardcodedPassword.equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }
}
