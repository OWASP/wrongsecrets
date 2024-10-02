package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This is a challenge based on using weak KDF to protect secrets. */
@Slf4j
@Component
@Order(49)
public class Challenge49 implements Challenge {

  private final String cipherText;
  private final String pin;

  public Challenge49(
      @Value("${challenge49ciphertext}") String cipherText,
      @Value("${challenge49pin}") String pin) {
    this.cipherText = cipherText;
    this.pin = pin;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(base64Decode(pin));
  }

  @Override
  public boolean answerCorrect(String answer) {
    String plainText = "the answer";

    try {
      int enteredPin = Integer.parseInt(answer);
      if (enteredPin < 0 || enteredPin > 99999) {
        return false;
      }
    } catch (Exception e) {
      log.warn("given answer is not an integer", e);
      return false;
    }

    try {
      String md5Hash = hashWithMd5(answer);
      return decrypt(cipherText, md5Hash).equals(plainText);
    } catch (Exception e) {
      log.warn("there was an exception with hashing content in challenge49", e);
      return false;
    }
  }

  @SuppressFBWarnings(
      value = "WEAK_MESSAGE_DIGEST_MD5",
      justification = "This is to allow md5 hashing")
  private String hashWithMd5(String plainText) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");

    byte[] result = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
    StringBuilder hexString = new StringBuilder();
    for (byte b : result) {
      hexString.append(String.format("%02x", b));
    }
    return hexString.toString();
  }

  @SuppressFBWarnings(
      value = {"CIPHER_INTEGRITY", "ECB_MODE"},
      justification = "This is to allow ecb encryption")
  private String decrypt(String cipherText, String key) {
    try {
      byte[] decodedEncryptedText = Base64.getDecoder().decode(cipherText);

      SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);

      byte[] decryptedData = cipher.doFinal(decodedEncryptedText);

      return new String(decryptedData, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("there was an exception with decrypting content in challenge49", e);
      return DECRYPTION_ERROR;
    }
  }

  private String base64Decode(String base64) {
    byte[] decodedBytes = Base64.getDecoder().decode(base64);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }
}
