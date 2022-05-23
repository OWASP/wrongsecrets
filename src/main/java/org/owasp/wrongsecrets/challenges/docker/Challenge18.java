package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(18)
public class Challenge18 extends Challenge {

    public Challenge18(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler("hunter2");
    }

    @Override
    public boolean answerCorrect(String answer) {
        return "hunter2".equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }
}
