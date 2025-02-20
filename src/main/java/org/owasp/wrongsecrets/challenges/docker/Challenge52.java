package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import groovy.util.logging.Slf4j;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Challenge52 extends FixedAnswerChallenge {

  private static final Logger log = LoggerFactory.getLogger(Challenge52.class);
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
