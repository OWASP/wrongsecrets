package org.owasp.wrongsecrets.challenges.cloud;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
@ChallengeNumber("9")
@Slf4j
public class Challenge9 extends Challenge {

    private final String awsDefaultValue;
    private final String challengeAnswer;
    private final String k8sEnvironment;

    public Challenge9(final ScoreCard scoreCard,
                      final @Value("${secretmountpath}") String filePath,
                      final @Value("${default_aws_value}") String awsDefaultValue,
                      final @Value("${K8S_ENV}") String k8sEnvironment) {
        super(scoreCard, ChallengeEnvironment.CLOUD);
        this.awsDefaultValue = awsDefaultValue;
        this.challengeAnswer = getCloudChallenge9and10Value(filePath, "wrongsecret");
        this.k8sEnvironment = k8sEnvironment;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(challengeAnswer);
    }

    @Override
    public boolean answerCorrect(final String answer) {
        return challengeAnswer.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return k8sEnvironment.equals("gcp") || k8sEnvironment.contains("aws");
    }

    private String getCloudChallenge9and10Value(final String filePath, final String fileName) {
        try {
            return Files.readString(Paths.get(filePath, fileName));
        } catch (Exception e) {
            log.warn("Exception during file reading, defaulting to default without cloud environment");
            return awsDefaultValue;
        }
    }
}
