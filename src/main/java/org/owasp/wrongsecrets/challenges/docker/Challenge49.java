package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Challenge49 extends FixedAnswerChallenge {

    private final String dockerSecret;

    public Challenge49(@Value("${DOCKER_SECRET_CHALLENGE49}") String dockerSecret) {
        this.dockerSecret = dockerSecret;
    }

    @Override
    public String getAnswer() {
        return this.dockerSecret;
    }
}
