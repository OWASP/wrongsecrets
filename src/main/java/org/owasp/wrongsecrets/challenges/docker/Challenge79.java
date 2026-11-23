package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/**
 * Challenge about a GitHub Actions secret that is made "forkable": because repository secrets are
 * not copied to forks, the workflow {@code .github/workflows/challenge79.yml} ships the secret
 * encrypted and keeps the AES key and IV right next to it. Everybody who forks the repository gets
 * the ciphertext and the key, so the encryption protects nothing.
 */
@Slf4j
@Component
public class Challenge79 extends FixedAnswerChallenge {

  // Must stay identical to the env values in .github/workflows/challenge79.yml (checked by a test).
  static final String CIPHERTEXT =
      "A8rW/GHqeiATV7gXaHoBku1+eF7zzu8NuAD4BDwAYPGDelPmPq38pi66/qE1ERh1hMTg1G5Lxn0=";
  static final String KEY = "8fa2807c0391c43fb804d8c00a6267f486c0201492f85d668390100b4463cdc5";
  static final String IV = "38242bf02823f7fd9557b2570e97c1c1";

  @Override
  @SuppressFBWarnings(
      value = "CIPHER_INTEGRITY",
      justification =
          "AES-CTR matches the openssl enc command in the challenge workflow, which has no GCM")
  public String getAnswer() {
    try {
      var cipher = Cipher.getInstance("AES/CTR/NoPadding");
      cipher.init(
          Cipher.DECRYPT_MODE,
          new SecretKeySpec(HexFormat.of().parseHex(KEY), "AES"),
          new IvParameterSpec(HexFormat.of().parseHex(IV)));
      return new String(
          cipher.doFinal(Base64.getDecoder().decode(CIPHERTEXT)), StandardCharsets.UTF_8);
    } catch (GeneralSecurityException | IllegalArgumentException e) {
      log.warn("Could not decrypt the secret of challenge 79", e);
      return DECRYPTION_ERROR;
    }
  }
}
