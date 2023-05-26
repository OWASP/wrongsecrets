package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about AWS keys in git history, with actual canarytokens. */
@Slf4j
@Component
@Order(15)
public class Challenge15 extends Challenge {

  private final String ciphterText;
  private final String encryptionKey;

  public Challenge15(ScoreCard scoreCard, @Value("${challenge15ciphertext}") String ciphterText) {
    super(scoreCard);
    this.ciphterText = ciphterText;
    encryptionKey =
        Base64.getEncoder().encodeToString("this is it for now".getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(quickDecrypt(ciphterText));
  }

  /** {@inheritDoc} */
  @Override
  protected boolean answerCorrect(String answer) {
    String correctString = quickDecrypt(ciphterText);
    return answer.equals(correctString) || minimummatch_found(answer);
  }

  @Override
  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.NORMAL;
  }

  /** {@inheritDoc} Git based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.GIT.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  private boolean minimummatch_found(String answer) {
    if (!Strings.isNullOrEmpty(answer)) {
      if (answer.length() < 19) {
        return false;
      }
      return quickDecrypt(ciphterText).contains(answer);
    }
    return false;
  }

  private String quickDecrypt(String cipherText) {
    try {
      final byte[] keyData = Base64.getDecoder().decode(encryptionKey);
      int aes256KeyLengthInBytes = 16;
      byte[] key = new byte[aes256KeyLengthInBytes];
      System.arraycopy(keyData, 0, key, 0, 16);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
      int gcmTagLengthInBytes = 16;
      int gcmIVLengthInBytes = 12;
      byte[] initializationVector = new byte[gcmIVLengthInBytes];
      Arrays.fill(
          initializationVector,
          (byte) 0); // done for "poor-man's convergent encryption", please check actual convergent
      // cryptosystems for better implementation ;-)
      GCMParameterSpec gcmParameterSpec =
          new GCMParameterSpec(gcmTagLengthInBytes * 8, initializationVector);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
      byte[] plainTextBytes =
          cipher.doFinal(Base64.getDecoder().decode(cipherText.getBytes(StandardCharsets.UTF_8)));
      return new String(plainTextBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception with Challenge 15", e);
      return "";
    }
  }

  // arcane:114,74
  // qemGhPXJjmipa9O7cYBJnuO79BQg/MgvSFbV9rhiBFuEmVqEfDsuz6xfBDMV2lH8TAhwKX39OrW+WIYxgaEWl8c1/n93Yxz5G/ZKbuTBbEaJ58YvC88IoB4NtnQciU6p+uJ+P+uHMMzRGQ0oGNvQeb5+bKK9V62Rp4aOhDupHnjeTUPKmWUV9/lzC5IUM7maNGuBLllzJnoM6QHMnGe5YpBBEA==
  // wrongsecrets:115,75
  // qcyRgfXSh0HUKsW/Xb5LnuWt9DgU8tQJfluR66UDDlmMgVWCGEwk1qxKAzUcpzb0KWQxP3nRFqO4SZEgqp8Ul8Ej/lNDbQCgBuszE/3WTn+g09Q7HcVUphA8g0Atg1GG4MpoepL8QOnhC0wxKMuqbe9TCu2nVqmUptKTmXGwAnmQH1TIl2MUueRuXpRKe72IMzKen1ArbMZqhu0I2HivROZgCUo=
  // wrongsecrets-2:115,75
  // qcyRgfXSh0HUKsW/Xb5LnuWt9DgU8tQJfluR66UDDlmMgVWCGEwk1qxKCi4ZvzDwM38xP3nRFqO4SZEgqp8Ul8Ej/lNDbQCgBuszSILVSV6D9eojOMl6zTcNgzUmjW2K3dJKN9LqXOLYezEpEN2gUaYqPu2nVqmUptKTmXGwAnmQH1TIl2MUueRuXpRKe72IMzKenxZHKRsNFp+ebQebS3qzP+Q=

}
