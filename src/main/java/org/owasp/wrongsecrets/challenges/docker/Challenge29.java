package org.owasp.wrongsecrets.challenges.docker;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.List;
//import java.net.HttpURLConnection;
//import java.net.URL;
import java.util.Random;
//import java.util.Scanner;

@Component
@Order(29)


public class Challenge29 extends Challenge {
    private final Random secureRandom = new SecureRandom();
    private static final String alphabet = "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
    public String sol = getMyString();

    public String getMyString() {
        String randomValue = generateRandomString(10);
        return randomValue;
    }

    public String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
        }
        return new String(builder);
    }


    public Challenge29(ScoreCard scoreCard) {

        super(scoreCard);
    }


    @Override
    public boolean canRunInCTFMode() {
        return true;
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(sol);
    }

    @Override
    public boolean answerCorrect(String answer) {
        return sol.equals(answer);
    }



    @Override
    public int difficulty() {
        return 2;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.FRONTEND.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }


    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }





}
