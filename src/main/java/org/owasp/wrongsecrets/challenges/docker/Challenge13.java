package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Challenge focused on showing CI/CD issues through Github Actions.
 */
@Slf4j
@Component
@Order(13)
public class Challenge13 extends Challenge {

    private final String plainText;
    private final String cipherText;

    /**
     * {@inheritDoc}
     */
    @Override
    public Spoiler spoiler() {
        String answer = Base64.getEncoder().encodeToString("This is our first key as github secret".getBytes(StandardCharsets.UTF_8));
        return new Spoiler(answer);
    }

    public Challenge13(ScoreCard scoreCard, @Value("${plainText13}") String plainText, @Value("${cipherText13}") String cipherText) {
        super(scoreCard);
        this.plainText = plainText;
        this.cipherText = cipherText;
    }

    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean answerCorrect(String answer) {
        return isKeyCorrect(answer);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    /**
     * {@inheritDoc}
     * Difficulty: 3
     */
    @Override
    public int difficulty() {
        return 3;
    }

    /**
     * {@inheritDoc}
     * CI/CD based.
     */
    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.CICD.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

    private boolean isKeyCorrect(String base64EncodedKey) {
        if (Strings.isEmpty(base64EncodedKey) || Strings.isEmpty(plainText) || Strings.isEmpty(cipherText)) {
            //log.debug("Checking secret with values {}, {}, {}", base64EncodedKey, plainText, cipherText);
            return false;
        }

        try {
            final byte[] keyData = Base64.getDecoder().decode(base64EncodedKey);
            int aes256KeyLengthInBytes = 16;
            byte[] key = new byte[aes256KeyLengthInBytes];
            System.arraycopy(keyData, 0, key, 0, 16);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            int gcmTagLengthInBytes = 16;
            int gcmIVLengthInBytes = 12;
            byte[] initializationVector = new byte[gcmIVLengthInBytes];
            Arrays.fill(initializationVector, (byte) 0); //done for "poor-man's convergent encryption", please check actual convergent cryptosystems for better implementation ;-)
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(gcmTagLengthInBytes * 8, initializationVector);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            byte[] cipherTextBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return cipherText.equals(Base64.getEncoder().encodeToString(cipherTextBytes));
        } catch (Exception e) {
            log.warn("Exception with Challenge 13", e);
            return false;
        }

    }

}
