package org.owasp.wrongsecrets.challenges.docker;

import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;

public class Challenge51 extends FixedAnswerChallenge {
  private final String dockerSecret;

  public Challenge51(@Value("${DOCKER_SECRET_CHALLENGE51}") String dockerSecret) {
    this.dockerSecret = dockerSecret;
  }

  @Override
  public String getAnswer() {
    return this.dockerSecret;
  }
}
