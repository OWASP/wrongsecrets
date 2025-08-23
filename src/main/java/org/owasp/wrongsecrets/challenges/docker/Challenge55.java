package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Challenge with a secret in .ssh/authorized_keys */
@Slf4j
@Component
public class Challenge55 implements Challenge {

  private final String basionhostpath;
  private String actualData;

  public Challenge55(@Value("${BASTIONHOSTPATH}") String basionhostpath) {
    this.basionhostpath = basionhostpath;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getActualData());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return !Strings.isNullOrEmpty(answer)
        && answer.length() > 10
        && (answer.contains(getActualData())
            || getActualData()
                .replace("\r", "")
                .replace("\n", "")
                .replace(" ", "")
                .contains(answer.replace("\r", "").replace("\n", "").replace(" ", "")));
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The location of the basionhostpath is based on an Env Var")
  private String getActualData() {
    if (Strings.isNullOrEmpty(actualData)) {
      try {
        actualData =
            Files.readString(
                Paths.get(basionhostpath, "wrongsecrets.keys"), StandardCharsets.UTF_8);
      } catch (Exception e) {
        log.warn(
            "Exception during file reading, defaulting to default without a docker container"
                + " environment",
            e);
        return FILE_MOUNT_ERROR;
      }
    }
    return actualData;
  }
}
