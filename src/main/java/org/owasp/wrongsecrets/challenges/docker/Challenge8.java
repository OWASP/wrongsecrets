package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Slf4j
@Component
@Order(8)
public class Challenge8 extends Challenge {

    private static final String alphabet = "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";

    private final Random secureRandom = new SecureRandom();
    private final String randomValue;

    public Challenge8(ScoreCard scoreCard) {
        super(scoreCard);
        randomValue = generateRandomString(10);
        log.info("Initializing challenge 8 with random value {}", randomValue);
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(randomValue);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return randomValue.equals(answer);
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    @Override
    public int difficulty() {
        return 2;
    }

    private String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
        }
        return new String(builder);
    }
}
