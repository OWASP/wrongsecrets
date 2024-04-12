package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.DECRYPTION_ERROR;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.stereotype.Component;

/** This is a challenge based on the idea of leaking a secret trough a vulnerability report. */
@Slf4j
@Component
public class Challenge35 extends FixedAnswerChallenge {

  @Override
  public String getAnswer() {
    return getKey();
  }

  private String getKey() {
    String ciphertext = "zRR77ETjg5GsXv3az1TZU73xiFWYHbVceJBvBbjChxLyMjHkF6kFdwIXIduVBHAT";
    try {
      return decrypt(ciphertext);
    } catch (Exception e) {
      log.warn("there was an exception with decrypting content in challenge35", e);
      return DECRYPTION_ERROR;
    }
  }

  @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
      value = "CIPHER_INTEGRITY",
      justification =
          "The scheme is bad without hmac, but we wanted to make it a bit more fun for you")
  private String decrypt(String ciphertext)
      throws InvalidAlgorithmParameterException,
          InvalidKeyException,
          NoSuchPaddingException,
          NoSuchAlgorithmException,
          IllegalBlockSizeException,
          BadPaddingException {
    IvParameterSpec iv = new IvParameterSpec("1234567890123456".getBytes(StandardCharsets.UTF_8));
    SecretKeySpec skeySpec =
        new SecretKeySpec(
            "12345678901234561234567890123456".getBytes(StandardCharsets.UTF_8), "AES");

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
    return new String(
        cipher.doFinal(Base64.decode(ciphertext.getBytes(StandardCharsets.UTF_8))),
        StandardCharsets.UTF_8);
  }
}
