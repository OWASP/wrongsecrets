package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.Spoiler;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("1")
public class Challenge1 extends Challenge {

    @Value("${password}")
    private String hardcodedPassword;
    @Value("${ARG_BASED_PASSWORD}")
    private String argBasedPassword;

    public Challenge1(ScoreCard scoreCard) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(hardcodedPassword);
    }

    @Override
    public boolean solved(String answer) {
        return hardcodedPassword.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return "if_you_see_this_please_use_docker_instead".equals(argBasedPassword);
    }
}
