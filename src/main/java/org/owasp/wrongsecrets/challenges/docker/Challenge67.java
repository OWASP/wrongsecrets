package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Challenge for finding secrets leaked in Dev Container configurations and environments. */
@Component
public class Challenge67 extends FixedAnswerChallenge {

  private final String devcontainerSecret;

  /**
   * Constructor for creating a new Challenge67 object.
   *
   * @param devcontainerSecret The secret configured in the Dev Container environment.
   */
  public Challenge67(
      @Value("${DEVCONTAINER_SECRET:WSECR-devcontainer-token-774921}") String devcontainerSecret) {
    this.devcontainerSecret = devcontainerSecret;
  }

  @Override
  public String getAnswer() {
    return this.devcontainerSecret;
  }
}
