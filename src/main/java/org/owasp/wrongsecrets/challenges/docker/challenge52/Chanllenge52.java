package org.owasp.wrongsecrets.challenges.docker.challenge52;

import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class Challenge52 extends FixedAnswerChallenge {

    // Replace these with your actual encrypted secret and encryption key
    private static final String ENCRYPTED_SECRET = "DefaultLoginPasswordDoNotChange!";
    private static final String ENCRYPTION_KEY = "mISydD0En55Fq8FXbUfX720K8Vc6/aQYtkFmkp7ntsM=Y";

    @Override
    public String getAnswer() {
        return decrypt(ENCRYPTED_SECRET, ENCRYPTION_KEY);
    }

    private String decrypt(String encryptedText, String base64Key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));

            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            return null;
        }
    }
}
