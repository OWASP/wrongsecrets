package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class Challenge13 extends Challenge {

    private String plainText;
    private String cipherText;

    @Override
    public Spoiler spoiler() {
        return null;
    }

    public Challenge13(ScoreCard scoreCard, @Value("${plainText13}") String plainText, @Value("${cipherText13}") String cipherText) {
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
//VGhpcyBpcyBvdXIgZmlyc3Qga2V5IGFzIGdpdGh1YiBzZWNyZXQK
        try {
            final byte[] keyData = Base64.getDecoder().decode(base64EncodedKey);
            byte[] key = new byte[16];
            System.arraycopy(keyData, 0, key, 0, 16);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            int GCM_TAG_LENGTH = 16;
            int GCM_IV_LENGTH = 12;
            byte[] IV = new byte[GCM_IV_LENGTH];
            Arrays.fill(IV, (byte) 0);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            byte[] cipherTextBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return cipherText.equals(Base64.getEncoder().encodeToString(cipherTextBytes));
        } catch (Exception e) {
            log.error("Exception with challnege 13", e);
            return false;
        }

    }
}
