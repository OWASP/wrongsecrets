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
@Order(4)
public class Challenge4 extends Challenge {

    private final String argBasedPassword;

    public Challenge4(ScoreCard scoreCard, @Value("${ARG_BASED_PASSWORD}") String argBasedPassword) {
        super(scoreCard);
        this.argBasedPassword = argBasedPassword;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(argBasedPassword);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return argBasedPassword.equals(answer) 
        || argBasedPassword.equals("'${answer}'");
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

}
