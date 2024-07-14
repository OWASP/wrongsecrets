package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * This is a challenge based on leaking secrets due to keeping the encryption key and secret
 * together.
 */
@Slf4j
@Component
public class Challenge40 extends FixedAnswerChallenge {

  private final Resource resource;

  public Challenge40(@Value("classpath:executables/secrchallenge.json") Resource resource) {
    this.resource = resource;
  }

  @Override
  public String getAnswer() {
    return getSolution();
  }

  @SuppressFBWarnings(
      value = {"CIPHER_INTEGRITY", "ECB_MODE"},
      justification = "This is to allow for easy ECB online decryptors")
  private String getSolution() {
    try {
      String jsonContent = resource.getContentAsString(Charset.defaultCharset());
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonNode = objectMapper.readTree(jsonContent);
      String encryptedText = jsonNode.get("secret").asText();
      byte[] decodedEncryptedText = Base64.getDecoder().decode(encryptedText.trim());
      byte[] plainDecryptionKey = jsonNode.get("key").asText().getBytes(StandardCharsets.UTF_8);

      // Create a SecretKey from the plaintext key
      SecretKey secretKey = new SecretKeySpec(plainDecryptionKey, "AES");

      // Initialize the Cipher for decryption
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);

      // Decrypt the data
      byte[] decryptedData = cipher.doFinal(decodedEncryptedText);

      // Convert the decrypted bytes to a String
      return new String(decryptedData, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("there was an exception with decrypting content in challenge40", e);
      return DECRYPTION_ERROR;
    }
  }
}
