package org.owasp.wrongsecrets.challenges.docker;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** This challenge is about having secrets in copied in bash history as part of a container. */
@Slf4j
@Component
public class Challenge17 extends FixedAnswerChallenge {

  private final String dockerMountPath;

  public Challenge17(@Value("${challengedockermtpath}") String dockerMountPath) {
    this.dockerMountPath = dockerMountPath;
  }

  @Override
  public String getAnswer() {
    return getActualData();
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the dockerMountPath is based on an Env Var")
  private String getActualData() {
    try {
      return Files.readString(Paths.get(dockerMountPath, "thirdkey.txt"), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn("Exception during file reading, defaulting to default without cloud environment", e);
      return "if_you_see_this_please_use_docker_instead";
    }
  }
}
