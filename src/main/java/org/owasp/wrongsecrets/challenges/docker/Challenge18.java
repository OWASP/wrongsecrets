package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

/** This challenge is about finding the value of a secret through weak hash mechanisms. */
@Slf4j
@Component
public class Challenge18 implements Challenge {

  private final String hashPassword;
  private static final String md5Hash = "MD5";
  private static final String sha1Hash = "SHA1";

  public Challenge18(@Value("aHVudGVyMg==") String hashPassword) {
    this.hashPassword = hashPassword;
  }

  private String base64Decode(String base64) {
    byte[] decodedBytes = Base64.getDecoder().decode(base64);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  private String calculateHash(String hash, String input) {
    try {
      if (md5Hash.equals(hash) || sha1Hash.equals(hash)) {
        var md = MessageDigest.getInstance(hash);
        return new String(Hex.encode(md.digest(input.getBytes(StandardCharsets.UTF_8))));
      }
    } catch (NoSuchAlgorithmException e) {
      log.warn("Exception thrown when calculating hash", e);
    }
    return Challenges.ErrorResponses.DECRYPTION_ERROR;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(base64Decode(hashPassword));
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return calculateHash(md5Hash, base64Decode(hashPassword)).equals(calculateHash(md5Hash, answer))
        || calculateHash(sha1Hash, base64Decode(hashPassword))
            .equals(calculateHash(sha1Hash, answer));
  }
}
