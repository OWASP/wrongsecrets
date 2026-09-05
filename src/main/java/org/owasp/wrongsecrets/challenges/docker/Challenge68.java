package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Challenge based on a secret that is hardcoded in a Claude skill. The skill is distributed as a
 * zip bundle, and the token does not sit in the {@code SKILL.md} itself but in one of the bundled
 * scripts, base64 encoded to keep secret scanners quiet.
 */
@Slf4j
@Component
public class Challenge68 extends FixedAnswerChallenge {

  private static final Pattern UPLOAD_TOKEN_PATTERN =
      Pattern.compile("UPLOAD_TOKEN_B64\\s*=\\s*\"([^\"]+)\"");

  private final Resource uploaderScript;

  public Challenge68(
      @Value(
              "classpath:challenges/challenge-68/claude-skill/incident-reporter/scripts/upload_report.py")
          Resource uploaderScript) {
    this.uploaderScript = uploaderScript;
  }

  @Override
  public String getAnswer() {
    try {
      var scriptContent = uploaderScript.getContentAsString(StandardCharsets.UTF_8);
      var matcher = UPLOAD_TOKEN_PATTERN.matcher(scriptContent);
      if (!matcher.find()) {
        log.warn("Could not find the upload token in the Claude skill of challenge 68");
        return FILE_MOUNT_ERROR;
      }
      return new String(Base64.getDecoder().decode(matcher.group(1)), StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.warn("Exception while reading the Claude skill of challenge 68", e);
      return FILE_MOUNT_ERROR;
    } catch (IllegalArgumentException e) {
      log.warn("The upload token in the Claude skill of challenge 68 is not valid base64", e);
      return FILE_MOUNT_ERROR;
    }
  }
}
