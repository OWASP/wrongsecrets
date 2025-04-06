package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** Challenge with a secret in .gitignore */
@Component
@Slf4j
public class Challenge54 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return decryptAES();
  }

  private String decryptAES() {
    final String encryptedSecret = "D7/KHlnFd5J3IXL+CF+TeLKrO3g99lzbOmLGYhdxxRw=";
    final String passphrase = "key_to_decrypt_the_secret";
    try {
      MessageDigest sha = MessageDigest.getInstance("SHA-256");
      byte[] key = sha.digest(passphrase.getBytes(StandardCharsets.UTF_8));
      SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      byte[] encryptedBytes = cipher.doFinal(encryptedSecret.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encryptedBytes);
    } catch (Exception e) {
      log.warn("Exception with Challenge 54", e);
      return DECRYPTION_ERROR;
    }
  }
}
