package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Challenge about exposed docker compose secrets * */
@Slf4j
@Component
public class Challenge52 extends FixedAnswerChallenge {

  private final String dockerMountsecret;

  public Challenge52(@Value("${chalenge_docker_mount_secret}") String dockerMountsecret) {
    this.dockerMountsecret = dockerMountsecret;
  }

  @Override
  public String getAnswer() {
    return getActualSecret();
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the dockerMountPath is based on an Env Var")
  private String getActualSecret() {
    try {
      return Files.readString(Paths.get(dockerMountsecret, "secret.txt"), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception during file reading, defaulting to default without cloud environment", e);
      return Challenges.ErrorResponses.OUTSIDE_DOCKER;
    }
  }
}
