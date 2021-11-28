package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("1")
public class Challenge1 extends Challenge {

    private final String hardcodedPassword;
    private final String argBasedPassword;

    public Challenge1(ScoreCard scoreCard, @Value("${password}") String hardcodedPassword, @Value("${ARG_BASED_PASSWORD}") String argBasedPassword) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
        this.hardcodedPassword = hardcodedPassword;
        this.argBasedPassword = argBasedPassword;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(hardcodedPassword);
    }

    @Override
    public String getExplanationFile() {
        return "challenge__1__.adoc";
    }

    @Override
    public boolean answerCorrect(String answer) {
        return hardcodedPassword.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return !"if_you_see_this_please_use_docker_instead".equals(argBasedPassword);
    }
}
