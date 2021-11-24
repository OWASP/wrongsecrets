package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.Spoiler;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("3")
public class Challenge3 extends Challenge {

    @Value("${DOCKER_ENV_PASSWORD}")
    private String hardcodedEnvPassword;

    public Challenge3(ScoreCard scoreCard) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(hardcodedEnvPassword);
    }

    @Override
    public boolean solved(String answer) {
        return hardcodedEnvPassword.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return "if_you_see_this_please_use_docker_instead".equals(hardcodedEnvPassword);
    }
}
