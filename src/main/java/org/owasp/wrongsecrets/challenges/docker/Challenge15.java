package org.owasp.wrongsecrets.challenges.docker;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
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
@Order(15)
public class Challenge15 extends Challenge {

    private String[] preStoredEncryptedAccessKeys;

    public Challenge15(ScoreCard scoreCard) {
        super(scoreCard);
    }

    @Override
    public Spoiler spoiler() {
        return null;
    }

    @Override
    protected boolean answerCorrect(String answer) {
        return false;
    }

    @Override
    public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
        return List.of(RuntimeEnvironment.Environment.DOCKER);
    }

    /**
     * Arcane:
     * [Arcane]
     * aws_access_key_id = AKIAYVP4CIPPEMEC27B2
     * aws_secret_access_key = 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
     * output = json
     * region = us-east-2
     *
     * Arcane debug:
     * [default]
     * aws_access_key_id = AKIAYVP4CIPPJCJOPJWL
     * aws_secret_access_key = 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
     * output = json
     * region = us-east-2
     *
     * wrongsecrets debug:
     * [default]
     * aws_access_key_id = AKIAYVP4CIPPCXOWVNMW
     * aws_secret_access_key = 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
     * output = json
     * region = us-east-2
     */

    private boolean matchesEncryptedString(String base64Offered){
        try {
            final byte[] keyData = "v9y$B&E)H@MbQeThWmZq4t7w!z%C*F-J".getBytes(StandardCharsets.UTF_8);
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
            byte[] cipherTextBytes = cipher.doFinal(base64Offered.getBytes(StandardCharsets.UTF_8));
            String cipherText= Base64.getEncoder().encodeToString(cipherTextBytes);
            for(String encryptedAccessKey: preStoredEncryptedAccessKeys){
                if (cipherText.equals(encryptedAccessKey)){
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Exception with Challenge 15", e);
            return false;
        }
    }
}
