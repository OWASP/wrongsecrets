package org.owasp.wrongsecrets.challenges.docker.challenge63;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import com.google.common.base.Strings;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.stereotype.Component;

/**
 * Challenge demonstrating bad encryption practices: hardcoding both the encryption key and IV
 * directly in source code. Even though the secret is encrypted, the key is right here in the code,
 * making the encryption completely ineffective.
 */
@SuppressWarnings("java:S5542")
@SuppressFBWarnings(
  value = {"CIPHER_INTEGRITY", "PADDING_ORACLE"},
    justification = "Challenge intentionally demonstrates hardcoded key/IV and CBC weaknesses")
@Slf4j
@Component
public class Challenge63 implements Challenge {

  private static final String HARDCODED_KEY = "SuperSecretKey12";
  private static final String HARDCODED_IV = "InitVector123456";
  private static final String CIPHERTEXT = "TDPwOvcLsbCWV5erlk6OHFnlFoXNtdQOt2JQeq+i4Ho=";
  private String result;

  public Challenge63() {
    // explicit constructor required
  }

  @Override
  public Spoiler spoiler() {
    if (Strings.isNullOrEmpty(result)) {
      result = getAnswer();
    }
    return new Spoiler(result);
  }

  @Override
  public boolean answerCorrect(String answer) {
    if (Strings.isNullOrEmpty(result)) {
      result = getAnswer();
    }
    return result.equals(answer);
  }

  private String getAnswer() {
    try {
      byte[] keyBytes = HARDCODED_KEY.getBytes(StandardCharsets.UTF_8);
      byte[] ivBytes = HARDCODED_IV.getBytes(StandardCharsets.UTF_8);
      byte[] cipherBytes = Base64.getDecoder().decode(CIPHERTEXT);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
      // Intentionally using CBC mode to demonstrate padding oracle vulnerability
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
      byte[] decrypted = cipher.doFinal(cipherBytes);
      return new String(decrypted, StandardCharsets.UTF_8).trim();
    } catch (Exception e) {
      log.error("Decryption failed", e);
      return DECRYPTION_ERROR;
    }
  }
}
