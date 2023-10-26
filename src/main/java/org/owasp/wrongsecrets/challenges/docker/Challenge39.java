package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/** This is a challenge based on leaking secrets due to choice of filename as encryption key */
@Slf4j
@Component
@Order(39)
public class Challenge39 extends Challenge {

  private final Resource resource;

  public Challenge39(
      ScoreCard scoreCard, @Value("classpath:executables/secrchallenge.md") Resource resource) {
    super(scoreCard);
    this.resource = resource;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getSolution());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return getSolution().equals(answer);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EASY;
  }

  /** {@inheritDoc} Cryptography based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.CRYPTOGRAPHY.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  @SuppressFBWarnings(
      value = {"CIPHER_INTEGRITY", "ECB_MODE"},
      justification = "This is to allow for easy ECB online decryptors")
  private String getSolution() {
    try {
      String encryptedText = resource.getContentAsString(Charset.defaultCharset());
      byte[] decodedEncryptedText = Base64.getDecoder().decode(encryptedText.trim());
      String filename = resource.getFilename();
      if (filename == null) {
        log.warn("could not get filename from resource");
        return "error_decryption";
      }
      byte[] decodedKey = filename.getBytes(StandardCharsets.UTF_8);

      // Create a SecretKey from the plaintext key
      SecretKey secretKey = new SecretKeySpec(decodedKey, "AES");

      // Initialize the Cipher for decryption
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);

      // Decrypt the data
      byte[] decryptedData = cipher.doFinal(decodedEncryptedText);

      // Convert the decrypted bytes to a String
      return new String(decryptedData, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("there was an exception with decrypting content in challenge39", e);
      return "error_decryption";
    }
  }
}
