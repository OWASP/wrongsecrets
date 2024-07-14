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

/** This challenge is about having a secret obfuscated in the front-end code. */
@Slf4j
@Component
public class Challenge16 extends FixedAnswerChallenge {

  private final String dockerMountPath;

  public Challenge16(@Value("${challengedockermtpath}") String dockerMountPath) {
    this.dockerMountPath = dockerMountPath;
  }

  @Override
  public String getAnswer() {
    return getActualData();
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the dockerMountPath is based on an Env Var")
  public String getActualData() {
    try {
      return Files.readString(Paths.get(dockerMountPath, "secondkey.txt"), StandardCharsets.UTF_8)
          .strip();
    } catch (Exception e) {
      log.warn("Exception during file reading, defaulting to default without cloud environment", e);
      return Challenges.ErrorResponses.FILE_MOUNT_ERROR;
    }
  }
}
