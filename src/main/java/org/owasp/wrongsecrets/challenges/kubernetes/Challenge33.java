package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import com.google.common.base.Strings;
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

/**
 * Challenge that teaches something about converting/migrating a secret while opening the metadata.
 */
@Slf4j
@Component
public class Challenge33 implements Challenge {

  private final String secretSecret;

  public Challenge33(@Value("${CHALLENGE33}") String secretSecret) {
    this.secretSecret = secretSecret;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getSolution());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return getSolution().equals(answer) && !DECRYPTION_ERROR.equals(answer);
  }

  private String getSolution() {
    if ("if_you_see_this_please_use_k8s".equals(secretSecret)
        || Strings.isNullOrEmpty(secretSecret)) {
      return "if_you_see_this_please_use_k8s";
    }
    return decrypt(decrypt(decrypt(secretSecret)));
  }

  private String decrypt(String cipherTextString) {
    try {
      final Cipher decryptor = Cipher.getInstance("AES/GCM/NoPadding");
      SecretKey decryptKey =
          new SecretKeySpec("Letsencryptnow!!".getBytes(StandardCharsets.UTF_8), "AES");
      AlgorithmParameterSpec gcmIv =
          new GCMParameterSpec(128, Base64.decode(cipherTextString), 0, 12);
      decryptor.init(Cipher.DECRYPT_MODE, decryptKey, gcmIv);
      return new String(
          decryptor.doFinal(
              Base64.decode(cipherTextString), 12, Base64.decode(cipherTextString).length - 12),
          StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception in Challenge33", e);
      return DECRYPTION_ERROR;
    }
  }
}
