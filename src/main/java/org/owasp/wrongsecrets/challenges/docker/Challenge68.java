package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret shared in a ChatGPT chat link. */
@Slf4j
@Component
public class Challenge68 extends FixedAnswerChallenge {

  private static final String CIPHERTEXT =
      "etl6oWVrKiBvZWM6eLdvzdtkKz+AHrsJltbkl0JQ8SY1qZpQD+WqrOG1ckJfaDo=";

  @Override
  public String getAnswer() {
    try {
      byte[] keyBytes = "SuperSecretKey12".getBytes(StandardCharsets.UTF_8);
      byte[] ivBytes = "InitVector12".getBytes(StandardCharsets.UTF_8);
      byte[] cipherBytes = Base64.getDecoder().decode(CIPHERTEXT);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivBytes);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
      byte[] decrypted = cipher.doFinal(cipherBytes);
      return new String(decrypted, StandardCharsets.UTF_8).trim();
    } catch (Exception e) {
      log.error("Decryption failed", e);
      return DECRYPTION_ERROR;
    }
  }
}
