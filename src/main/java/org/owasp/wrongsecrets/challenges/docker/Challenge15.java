package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Challenge15 extends Challenge {

    private String plainText;
    private String cipherText;

    @Override
    public Spoiler spoiler() {
        return null;
    }

    public Challenge15(ScoreCard scoreCard, @Value("${plainText}") String plainText, @Value("${cipherText}") String cipherText) {
        super(scoreCard);
        this.plainText = plainText;
        this.cipherText = cipherText;
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return isKeyCorrect(answer);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    private boolean isKeyCorrect(String base64EncodedKey) {
        if (Strings.isEmpty(base64EncodedKey) || Strings.isEmpty(plainText) || Strings.isEmpty(cipherText)) {
            log.info("Checking secret with values {}, {}, {}", base64EncodedKey, plainText, cipherText);
            return false;
        }

        try {

            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
