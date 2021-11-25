package org.owasp.wrongsecrets.challenges.docker;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("3")
public class Challenge3 extends Challenge {

    private final String hardcodedEnvPassword;
    private final String argBasedPassword;

    public Challenge3(final ScoreCard scoreCard,
                      final @Value("${DOCKER_ENV_PASSWORD}") String hardcodedEnvPassword,
                      final @Value("${ARG_BASED_PASSWORD}") String argBasedPassword) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
        this.hardcodedEnvPassword = hardcodedEnvPassword;
        this.argBasedPassword = argBasedPassword;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(hardcodedEnvPassword);
    }

    @Override
    public boolean answerCorrect(final String answer) {
        return hardcodedEnvPassword.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return !"if_you_see_this_please_use_docker_instead".equals(argBasedPassword);
    }
}
