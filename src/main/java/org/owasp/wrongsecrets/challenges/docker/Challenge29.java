package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** This challenge is about finding a secret in a Github issue (screenshot). */
@Slf4j
@Component
public class Challenge29 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return decryptActualAnswer();
  }

  private byte[] decode(byte[] encoded, PrivateKey privateKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return cipher.doFinal(encoded);
  }

  @SuppressFBWarnings(
      value = "DMI_HARDCODED_ABSOLUTE_FILENAME",
      justification = "This is embededded in the container")
  private String getKey() throws IOException {
    String privateKeyFilePath = "src/test/resources/RSAprivatekey.pem";
    byte[] content;
    try {
      content = Files.readAllBytes(Paths.get(privateKeyFilePath));
    } catch (IOException e) {
      log.info("Could not get the file from {}", privateKeyFilePath);
      privateKeyFilePath = "/var/tmp/helpers/RSAprivatekey.pem";
      try {
        content = Files.readAllBytes(Paths.get(privateKeyFilePath));
      } catch (IOException e2) {
        log.info("Could not get the file from {}", privateKeyFilePath);
        throw e2;
      }
    }
    String privateKeyContent = new String(content, StandardCharsets.UTF_8);
    privateKeyContent = privateKeyContent.replace("-----BEGIN PRIVATE KEY-----", "");
    privateKeyContent = privateKeyContent.replace("-----END PRIVATE KEY-----", "");
    privateKeyContent = privateKeyContent.replaceAll("\\s", "");
    return privateKeyContent;
  }

  private String decryptActualAnswer() {
    try {
      String privateKeyContent = getKey();
      byte[] privateKeyBytes = java.util.Base64.getDecoder().decode(privateKeyContent);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PrivateKey privateKey = kf.generatePrivate(spec);

      byte[] encoded =
          java.util.Base64.getDecoder()
              .decode(
                  "aUb8RPnocWk17xXj0Xag8AOA8K0S4OD/jdqnIzMi5ItpEwPVLZUghYTGx53CHHb2LWRR+WH+Gx41Cr9522FbQDKbDMRaCd7GIMDApwUrFScevI/+usF0bmrw3tH9RUvCtxRZCDtsl038yNn90llsQM1e9OORMIvpzN1Ut0nDKErDvgv4pkUZXqGcybVKEGrULVWiIt8UYzd6lLNrRiRYrbcKrHNveyBhFExLpI/PsWS2NIcqyV7vXIib/PUBH0UdhSVnd+CJhNnFPBxQdScEDK7pYnhctr0I1Vl10Uk86uYsmMzqDSbt+TpCZeofcnd3tPdBB7z3c9ewVS+/fAVwlQ=="
                      .getBytes(StandardCharsets.UTF_8));
      byte[] decoded = decode(encoded, privateKey);
      return new String(decoded, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception when decrypting", e);
      return "decrypt_error";
    }
  }
}
