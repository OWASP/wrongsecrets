package org.owasp.wrongsecrets.challenges.kubernetes;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("5")
public class Challenge5 extends Challenge {

    private final String configmapK8sSecret;

    public Challenge5(ScoreCard scoreCard, @Value("${SPECIAL_K8S_SECRET}") String configmapK8sSecret) {
        super(scoreCard, ChallengeEnvironment.K8S);
        this.configmapK8sSecret = configmapK8sSecret;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(configmapK8sSecret);
    }

    @Override
    public String getExplanationFileIdentifier() {
        return "5";
    }

    @Override
    public boolean answerCorrect(String answer) {
        return configmapK8sSecret.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return !"if_you_see_this_please_use_k8s".equals(configmapK8sSecret);
    }
}
