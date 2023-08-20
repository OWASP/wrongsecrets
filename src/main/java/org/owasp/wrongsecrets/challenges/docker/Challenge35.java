package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This is a challenge based on the idea of leaking a secret trough a vulnerability report. */
@Slf4j
@Component
@Order(35)
public class Challenge35 extends Challenge {

  public Challenge35(ScoreCard scoreCard) {
    super(scoreCard);
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getKey());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return getKey().equals(answer);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EASY;
  }

  /** {@inheritDoc} This is a Documentation type of challenge */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.DOCUMENTATION.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  private String getKey() {
    String ciphertext = "zRR77ETjg5GsXv3az1TZU73xiFWYHbVceJBvBbjChxLyMjHkF6kFdwIXIduVBHAT";
    try {
      return decrypt(ciphertext);
    } catch (Exception e) {
      log.warn("there was an exception with decrypting content in challenge35", e);
      return "error_decryption";
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
