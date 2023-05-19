package org.owasp.wrongsecrets.challenges.kubernetes;

import static org.owasp.wrongsecrets.RuntimeEnvironment.Environment.K8S;

import com.google.common.base.Strings;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Challenge that teaches something about converting/migrating a secret while opening the metadata
 */
@Slf4j
@Component
@Order(33)
public class Challenge33 extends Challenge {

  private final String secretSecret;

  public Challenge33(ScoreCard scoreCard, @Value("${CHALLENGE33}") String secretSecret) {
    super(scoreCard);
    this.secretSecret = secretSecret;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getSolution());
  }

  @Override
  protected boolean answerCorrect(String answer) {
    return getSolution().equals(answer);
  }

  @Override
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(K8S);
  }

  @Override
  public int difficulty() {
    return Difficulty.NORMAL;
  }

  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.SECRETS.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  private String getSolution() {
    if ("if_you_see_this_please_use_k8s".equals(secretSecret)
        || Strings.isNullOrEmpty(secretSecret)) {
      return "if_you_see_this_please_use_k8s";
    }
    return decrypt(decrypt(decrypt(secretSecret)));
  }

  private String decrypt(String cipherTextString) {
    try {
      final Cipher decryptor = Cipher.getInstance("AES/GCM/NoPadding");
      SecretKey decryptKey =
          new SecretKeySpec("Letsencryptnow!!".getBytes(StandardCharsets.UTF_8), "AES");
      AlgorithmParameterSpec gcmIv =
          new GCMParameterSpec(128, Base64.decode(cipherTextString), 0, 12);
      decryptor.init(Cipher.DECRYPT_MODE, decryptKey, gcmIv);
      return new String(
          decryptor.doFinal(
              Base64.decode(cipherTextString), 12, Base64.decode(cipherTextString).length - 12),
          StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception in Challenge33", e);
      return "";
    }
  }
}
