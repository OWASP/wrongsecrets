package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import java.util.List;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.ScoreCard;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.ChallengeTechnology;
import org.owasp.wrongsecrets.challenges.Difficulty;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This challenge is about having a secrets stored in a misconfigured Hashicorp Vault. */
@Component
@Order(7)
public class Challenge7 extends Challenge {

  private final Vaultpassword vaultPassword;
  private final String vaultPasswordString;

  public Challenge7(
      ScoreCard scoreCard,
      Vaultpassword vaultPassword,
      @Value("${vaultpassword}") String vaultPasswordString) {
    super(scoreCard);
    this.vaultPassword = vaultPassword;
    this.vaultPasswordString = vaultPasswordString;
  }

  @Override
  public boolean canRunInCTFMode() {
    return true;
  }

  private String getAnswer() {
    return vaultPassword != null && !Strings.isNullOrEmpty(vaultPassword.getPasssword())
        ? vaultPassword.getPasssword()
        : vaultPasswordString;
  }

  /** {@inheritDoc} */
  @Override
  public Spoiler spoiler() {
    return new Spoiler(getAnswer());
  }

  /** {@inheritDoc} */
  @Override
  public boolean answerCorrect(String answer) {
    return getAnswer().equals(answer);
  }

  /** {@inheritDoc} */
  public List<RuntimeEnvironment.Environment> supportedRuntimeEnvironments() {
    return List.of(RuntimeEnvironment.Environment.VAULT);
  }

  /** {@inheritDoc} */
  @Override
  public int difficulty() {
    return Difficulty.EXPERT;
  }

  /** {@inheritDoc} Vault based. */
  @Override
  public String getTech() {
    return ChallengeTechnology.Tech.VAULT.id;
  }

  @Override
  public boolean isLimitedWhenOnlineHosted() {
    return false;
  }
}
