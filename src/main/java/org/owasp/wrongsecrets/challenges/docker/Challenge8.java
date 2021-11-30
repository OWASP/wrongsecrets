package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeEnvironment;
import org.owasp.wrongsecrets.challenges.ChallengeNumber;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@Component
@ChallengeNumber("8")
public class Challenge8 extends Challenge {

    private final Random secureRandom = new SecureRandom();
    private final String alphabet = "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
    private String randomValue;

    public Challenge8(ScoreCard scoreCard) {
        super(scoreCard, ChallengeEnvironment.DOCKER);
        randomValue = generateRandomString(10);
        log.info("Initializing challenge 8 with value {}", randomValue);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(randomValue);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return randomValue.equals(answer);
    }

    @Override
    public boolean environmentSupported() {
        return true;
    }

    private String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
        }
        return new String(builder);
    }
}
