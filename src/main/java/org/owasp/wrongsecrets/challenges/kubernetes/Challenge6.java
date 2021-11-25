package org.owasp.wrongsecrets.challenges.kubernetes;


import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChallengeNumber("6")
public class Challenge6 extends Challenge {

    private final String secretK8sSecret;

    public Challenge6(final ScoreCard scoreCard,
                      final @Value("${SPECIAL_SPECIAL_K8S_SECRET}") String secretK8sSecret) {
        super(scoreCard, ChallengeEnvironment.K8S);
        this.secretK8sSecret = secretK8sSecret;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(secretK8sSecret);
    }

    @Override
    public boolean answerCorrect(final String answer) {
        return secretK8sSecret.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return !"if_you_see_this_please_use_k8s".equals(secretK8sSecret);
    }
}
