package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** Challenge with a secret in .gitignore */
@Component
public class Challenge54 extends FixedAnswerChallenge {

  // Secret clair
  private static final String encryptedSecret = "D7/KHlnFd5J3IXL+CF+TeLKrO3g99lzbOmLGYhdxxRw=";

  private static final String passphrase = "key_to_decrypt_the_secret";

  private static SecretKeySpec getKeyFromPassphrase(String passphrase) throws Exception {
    MessageDigest sha = MessageDigest.getInstance("SHA-256");
    byte[] key = sha.digest(passphrase.getBytes(StandardCharsets.UTF_8));
    return new SecretKeySpec(key, "AES");
  }

  public static String encryptAES(String input) throws Exception {
    SecretKeySpec secretKey = getKeyFromPassphrase(passphrase);
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  @Override
  public String getAnswer() {
    return encryptedSecret;
  }
}
