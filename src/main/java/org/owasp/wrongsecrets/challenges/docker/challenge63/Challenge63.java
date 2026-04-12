package org.owasp.wrongsecrets.challenges.docker.challenge63;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/**
 * Challenge demonstrating bad encryption practices: hardcoding both the encryption key and IV
 * directly in source code. Even though the secret is encrypted, the key is right here in the code,
 * making the encryption completely ineffective.
 */
@SuppressWarnings("java:S5542")
@Slf4j
@Component
public class Challenge63 implements Challenge {

  private static final String HARDCODED_KEY = "SuperSecretKey12";
  private static final String HARDCODED_IV = "InitVector123456";
  private static final String CIPHERTEXT = "TDPwOvcLsbCWV5erlk6OHFnlFoXNtdQOt2JQeq+i4Ho=";

  public Challenge63() {
    // explicit constructor required
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getAnswer());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return getAnswer().equals(answer);
  }

  private String getAnswer() {
    try {
      byte[] keyBytes = HARDCODED_KEY.getBytes("UTF-8");
      byte[] ivBytes = HARDCODED_IV.getBytes("UTF-8");
      byte[] cipherBytes = Base64.getDecoder().decode(CIPHERTEXT);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
      // Intentionally using CBC mode to demonstrate padding oracle vulnerability
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
      byte[] decrypted = cipher.doFinal(cipherBytes);
      return new String(decrypted, "UTF-8").trim();
    } catch (Exception e) {
      log.error("Decryption failed", e);
      return "decryption-error";
    }
  }
}