package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Challenge focused on filesystem issues in docker container due to workdir copying. */
@Slf4j
@Component
public class Challenge12 extends FixedAnswerChallenge {

  private final String dockerMountPath;

  public Challenge12(@Value("${challengedockermtpath}") String dockerMountPath) {
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
      return Files.readString(Paths.get(dockerMountPath, "yourkey.txt"), StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.warn(
          "Exception during file reading, defaulting to default without a docker container"
              + " environment",
          e);
      return FILE_MOUNT_ERROR;
    }
  }
}
