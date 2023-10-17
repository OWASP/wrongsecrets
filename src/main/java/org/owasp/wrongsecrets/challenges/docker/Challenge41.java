package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** This is a challenge based on finding secret using password shucking */
@Slf4j
@Component
@Order(41)
public class Challenge41 extends Challenge {

  private final String password;

  public Challenge41(ScoreCard scoreCard, @Value("${challenge41password}") String password) {
    super(scoreCard);
    this.password = password;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(base64Decode(password));
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.HARD;
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
