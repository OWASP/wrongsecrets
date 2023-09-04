package org.owasp.wrongsecrets.challenges.docker;

import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
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

/**
 * This is a challenge based on LLM where poeople need to extract the secret from
 * https://https://gandalf.lakera.ai//
 */
@Slf4j
@Component
@Order(32)
public class Challenge32 extends Challenge {

  public Challenge32(ScoreCard scoreCard) {
    super(scoreCard);
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
    return Difficulty.NORMAL;
  }

  /** {@inheritDoc} This is an AI type of challenge */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.AI.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.DOCKER);
  }

  private String getSolution() {
    return decrypt(
        decrypt(
            decrypt(
                "BE8VK4p3wD7Rba3rA5bduOYG99yMhuyonyHC4JPm15VNHDU0ULORJB1oSBddWEWIA39oFP0osD+YVRX8zBZZeNdif9o1Prar3L1tbCc821PiOA6JOfZFOWscMTy0Plpo9jKsz8RBt4/Sp3xxJsjVaW+ZgBki+MeB7+rgUnK+elI5iu2E")));
  }

  private String decrypt(String cipherTextString) {
    try {
      final Cipher decryptor = Cipher.getInstance("AES/GCM/NoPadding");
      SecretKey decryptKey =
          new SecretKeySpec("AIKnowsThisKey12".getBytes(StandardCharsets.UTF_8), "AES");
      AlgorithmParameterSpec gcmIv =
          new GCMParameterSpec(128, Base64.decode(cipherTextString), 0, 12);
      decryptor.init(Cipher.DECRYPT_MODE, decryptKey, gcmIv);
      return new String(
          decryptor.doFinal(
              Base64.decode(cipherTextString.getBytes(StandardCharsets.UTF_8)),
              12,
              Base64.decode(cipherTextString.getBytes(StandardCharsets.UTF_8)).length - 12),
          StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception in Challenge32", e);
      return "";
    }
  }
}
