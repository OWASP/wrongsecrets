package org.owasp.wrongsecrets.challenges.docker;


import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@Order(25)
public class Challenge25 extends Challenge {
    private final String cipherText;
    private final String encryptionKey;

    public Challenge25(ScoreCard scoreCard, @Value("${challenge25ciphertext}") String cipherText) {
        super(scoreCard);
        this.cipherText = cipherText;
        encryptionKey = Base64.getEncoder().encodeToString("this is it for now".getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public boolean canRunInCTFMode() {
        return true;
    }

    @Override
    public Spoiler spoiler() {
        return new Spoiler(quickDecrypt(cipherText));
    }

    @Override
    public boolean answerCorrect(String answer) {
        String correctString = quickDecrypt(cipherText);
        return answer.equals(correctString);
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    @Override
    public int difficulty() {
        return 1;
    }

    @Override
    public String getTech() {
        return ChallengeTechnology.Tech.WEB3.id;
    }

    @Override
    public boolean isLimittedWhenOnlineHosted() {
        return false;
    }

    private String quickDecrypt(String cipherText) {
        try {
            final byte[] keyData = Base64.getDecoder().decode(encryptionKey);
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
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
            byte[] plainTextBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes(StandardCharsets.UTF_8)));
            return new String(plainTextBytes);
        } catch (Exception e) {
            log.warn("Exception with Challenge 25", e);
            return "";
        }
    }
}
