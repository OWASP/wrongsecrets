package org.owasp.wrongsecrets.challenges.docker;

import com.google.common.base.Strings;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

/** Challenge focused on showing CI/CD issues through Github Actions. */
@Slf4j
@Component
@Order(13)
public class Challenge13 extends Challenge {

  private final String plainText;
  private final String cipherText;

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    String answer =
        Base64.getEncoder()
            .encodeToString(
                "This is our first key as github secret".getBytes(StandardCharsets.UTF_8));
    return new Spoiler(answer);
  }

  public Challenge13(
      ScoreCard scoreCard,
      @Value("${plainText13}") String plainText,
      @Value("${cipherText13}") String cipherText) {
    super(scoreCard);
    this.plainText = plainText;
    this.cipherText = cipherText;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  protected boolean answerCorrect(String answer) {
    return isKeyCorrect(answer);
  }

  /** {@inheritDoc} */
  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.HARD;
  }

  /** {@inheritDoc} CI/CD based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.CICD.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  private boolean isKeyCorrect(String base64EncodedKey) {
    if (Strings.isNullOrEmpty(base64EncodedKey)
        || !isBase64(base64EncodedKey)
        || Strings.isNullOrEmpty(plainText)
        || Strings.isNullOrEmpty(cipherText)) {
      // log.debug("Checking secret with values {}, {}, {}", base64EncodedKey, plainText,
      // cipherText);
      return false;
    }

    try {
      final byte[] keyData = Base64.getDecoder().decode(base64EncodedKey);
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
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
      byte[] cipherTextBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
      return cipherText.equals(Base64.getEncoder().encodeToString(cipherTextBytes));
    } catch (Exception e) {
      log.warn("Exception with Challenge 13", e);
      return false;
    }
  }

  private boolean isBase64(String text) {
    String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(text);
    return m.find();
  }
}
