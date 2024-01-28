package org.owasp.wrongsecrets.challenges.kubernetes;

import com.google.common.base.Strings;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about having a secrets injected via Vault template. */
@Component
public class Challenge46 extends FixedAnswerChallenge {

  private final Vaultinjected vaultinjected;
  private final String mockedAnswer;

  public Challenge46(Vaultinjected vaultinjected, @Value("${vaultinjected}") String mockedAnswer) {
    this.vaultinjected = vaultinjected;
    this.mockedAnswer = mockedAnswer;
  }

  @Override
  public String getAnswer() {
    return vaultinjected != null && !Strings.isNullOrEmpty(vaultinjected.getValue())
        ? vaultinjected.getValue()
        : mockedAnswer;
  }
}
