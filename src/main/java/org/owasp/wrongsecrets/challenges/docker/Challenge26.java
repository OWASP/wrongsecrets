package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret hardcoded in a web3 contract. */
@Slf4j
@Component
public class Challenge26 implements Challenge {
  private final String cipherText;

  public Challenge26(@Value("${challenge26ciphertext}") String cipherText) {
    this.cipherText = cipherText;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(quickDecrypt(cipherText));
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    String correctString = quickDecrypt(cipherText);
    return answer.equals(correctString) && !DECRYPTION_ERROR.equals(answer);
  }

  private String quickDecrypt(String cipherText) {
    try {
      final Cipher decryptor = Cipher.getInstance("AES/GCM/NoPadding");
      SecretKey decryptKey =
          new SecretKeySpec("thiszthekeytoday".getBytes(StandardCharsets.UTF_8), "AES");
      AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, Base64.decode(cipherText), 0, 12);
      decryptor.init(Cipher.DECRYPT_MODE, decryptKey, gcmIv);
      return new String(
          decryptor.doFinal(Base64.decode(cipherText), 12, Base64.decode(cipherText).length - 12),
          StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception with Challenge 26", e);
      return DECRYPTION_ERROR;
    }
  }
}
