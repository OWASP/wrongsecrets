package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret shared in a ChatGPT chat link. */
@Slf4j
@Component
public class Challenge67 extends FixedAnswerChallenge {

  private static final String CIPHERTEXT = "dagGnT4fdF7/z010sAHhhp1t6w2NsUQkHy9+Fx1LCG0=";

  @Override
  public String getAnswer() {
    try {
      byte[] keyBytes = "SuperSecretKey12".getBytes(StandardCharsets.UTF_8);
      byte[] ivBytes = "InitVector123456".getBytes(StandardCharsets.UTF_8);
      byte[] cipherBytes = Base64.getDecoder().decode(CIPHERTEXT);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
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
