package org.owasp.wrongsecrets.challenges.kubernetes;


import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.K8S;

@Component
public class Challenge6 extends Challenge {

    private final String secretK8sSecret;

    public Challenge6(ScoreCard scoreCard, @Value("${SPECIAL_SPECIAL_K8S_SECRET}") String secretK8sSecret) {
        super(scoreCard);
        this.secretK8sSecret = secretK8sSecret;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(secretK8sSecret);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return secretK8sSecret.equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(K8S);
    }
}
