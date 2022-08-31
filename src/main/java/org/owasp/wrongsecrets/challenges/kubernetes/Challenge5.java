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
@Order(5)
public class Challenge5 extends Challenge {

    private final String configmapK8sSecret;

    public Challenge5(ScoreCard scoreCard, @Value("${SPECIAL_K8S_SECRET}") String configmapK8sSecret) {
        super(scoreCard);
        this.configmapK8sSecret = configmapK8sSecret;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(configmapK8sSecret);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return configmapK8sSecret.equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(K8S);
    }

    @Override
    public int difficulty() {
        return 2;
    }

    @Override
    public String getTech() {
        return "Configmaps";
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return true;
    }
}
