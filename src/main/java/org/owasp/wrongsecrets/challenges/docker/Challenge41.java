package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** This is a challenge based on finding secret using password shucking. */
@Slf4j
@Component
@Order(41)
public class Challenge41 implements Challenge {

  private final String password;

  public Challenge41(@Value("${challenge41password}") String password) {
    this.password = password;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(base64Decode(password));
  }

  @Override
  public boolean answerCorrect(String answer) {
    try {
      BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
      String hash = bcrypt.encode(hashWithMd5(base64Decode(password)));
      String md5Hash = hashWithMd5(answer);
      return bcrypt.matches(md5Hash, hash);
    } catch (Exception e) {
      log.warn("there was an exception with hashing content in challenge41", e);
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

  private String base64Decode(String base64) {
    byte[] decodedBytes = Base64.getDecoder().decode(base64);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }
}
