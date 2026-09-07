package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Challenge for finding secrets leaked in build container layers. */
@Component
public class ChallengeBuildContainer extends Challenge {

  private final String buildSecret;

  public ChallengeBuildContainer(
      @Value("${build.container.secret:WSECR-build-layer-secret-849201}") String buildSecret) {
    this.buildSecret = buildSecret;
  }

  @Override
  public boolean answerCorrect(String answer) {
    return buildSecret != null && buildSecret.trim().equals(answer != null ? answer.trim() : "");
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(buildSecret);
  }
}
