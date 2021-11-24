package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("2")
public class Challenge2 extends Challenge {

    private final String argBasedPassword;

    public Challenge2(ScoreCard scoreCard, @Value("${ARG_BASED_PASSWORD}") String argBasedPassword) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
        this.argBasedPassword = argBasedPassword;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(argBasedPassword);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return argBasedPassword.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return !"if_you_see_this_please_use_docker_instead".equals(argBasedPassword);
    }
}
