package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Challenge for finding secrets leaked in build container layers. */
@Component
public class Challenge67 extends FixedAnswerChallenge {

  private final String buildSecret;

  /**
   * Constructor for creating a new Challenge67 object.
   *
   * @param buildSecret The build container secret.
   */
  public Challenge67(
      @Value("${build.container.secret:WSECR-build-layer-secret-849201}") String buildSecret) {
    this.buildSecret = buildSecret;
  }

  @Override
  public String getAnswer() {
    return this.buildSecret;
  }
}
