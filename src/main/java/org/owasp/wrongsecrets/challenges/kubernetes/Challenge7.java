package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about having a secrets stored in a misconfigured Hashicorp Vault. */
@Component
public class Challenge7 implements Challenge {

  private final Vaultpassword vaultPassword;
  private final String vaultPasswordString;

  public Challenge7(
      Vaultpassword vaultPassword, @Value("${vaultpassword}") String vaultPasswordString) {
    this.vaultPassword = vaultPassword;
    this.vaultPasswordString = vaultPasswordString;
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
}
