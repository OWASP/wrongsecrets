package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import com.google.common.base.Strings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Challenge to find a secret in the project-specification.mdc file. */
@Slf4j
@Component
public class Challenge56 implements Challenge {

  private final String projectSpecPath;
  private String actualSecret;

  public Challenge56(
      @Value("${projectspecpath:cursor/rules/project-specification.mdc}") String projectSpecPath) {
    this.projectSpecPath = projectSpecPath;
  }

  @Override
  public Spoiler spoiler() {
    return new Spoiler(getActualSecret());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return !Strings.isNullOrEmpty(answer) && getActualSecret().equals(answer.trim());
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "Intentional for educational purposes in this challenge; see documentation.")
  private String getActualSecret() {
    if (Strings.isNullOrEmpty(actualSecret)) {
      try {
        Path filePath = Paths.get(projectSpecPath);
        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        // Look for the line with the secret
        for (String line : content.split("\n")) {
          if (line.trim().startsWith("**secret-challenge-56:")) {
            actualSecret = line.split(":", 2)[1].trim();
            break;
          }
        }
        if (Strings.isNullOrEmpty(actualSecret)) {
          return FILE_MOUNT_ERROR;
        }
      } catch (Exception e) {
        log.warn("Exception during file reading for Challenge56", e);
        return FILE_MOUNT_ERROR;
      }
    }
    return actualSecret;
  }
}
