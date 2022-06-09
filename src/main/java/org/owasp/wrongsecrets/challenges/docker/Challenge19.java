package org.owasp.wrongsecrets.challenges.docker;


import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.DOCKER;

@Component
@Order(19)
@Slf4j
public class Challenge19 extends Challenge {

    public Challenge19(ScoreCard scoreCard) {
        super(scoreCard);
    }


    @Override
    public Spoiler spoiler() {
        return new Spoiler(executeCommand(""));
    }

    @Override
    public boolean answerCorrect(String answer) {
        return executeCommand(answer).equals("This is correct! Congrats!");
    }

    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(DOCKER);
    }

    private String executeCommand(String guess) {
        Runtime runTime = Runtime.getRuntime();
        if (Strings.isNullOrEmpty((guess))) {
            guess = "spoil";
        }
        try {
            String systemARch = System.getProperty("os.arch");
            log.info("System arch detected: {}", systemARch);
            String fileLocation;
            if (systemARch == "x86_64") {
                fileLocation = "wrongsecrets-c";
            } else {
                fileLocation = "wrongsecrets-c-arm";
            }
            String output = runTime.exec(new String[]{fileLocation, guess}).getOutputStream().toString();
            log.info("Output challenge 19: {}", output);
            return output;
        } catch (IOException e) {
            log.warn("Error executing:", e);
            return "Error with executing";
        }

    }
}
