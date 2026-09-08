package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Challenge based on a secret that is hardcoded in a Claude skill. The skill is distributed as an
 * exported {@code .skill} zip bundle, and the token does not sit in the {@code SKILL.md} itself but
 * in one of the bundled scripts, base64 encoded to keep secret scanners quiet.
 */
@Slf4j
@Component
public class Challenge71 extends FixedAnswerChallenge {

  private static final Pattern UPLOAD_TOKEN_PATTERN =
      Pattern.compile("UPLOAD_TOKEN_B64\\s*=\\s*\"([^\"]+)\"");

  private final Resource skillBundle;

  public Challenge71(
      @Value("classpath:challenges/challenge-71/claude-skill/incident-reporter.skill")
          Resource skillBundle) {
    this.skillBundle = skillBundle;
  }

  @Override
  public String getAnswer() {
    try (var zip = new ZipInputStream(skillBundle.getInputStream(), StandardCharsets.UTF_8)) {
      for (var entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
        if (entry.getName().endsWith("upload_report.py")) {
          var scriptContent = new String(zip.readAllBytes(), StandardCharsets.UTF_8);
          var matcher = UPLOAD_TOKEN_PATTERN.matcher(scriptContent);
          if (!matcher.find()) {
            log.warn("Could not find the upload token in the Claude skill of challenge 71");
            return FILE_MOUNT_ERROR;
          }
          return new String(Base64.getDecoder().decode(matcher.group(1)), StandardCharsets.UTF_8);
        }
      }
      log.warn("upload_report.py not found in the Claude skill bundle of challenge 71");
      return FILE_MOUNT_ERROR;
    } catch (IOException e) {
      log.warn("Exception while reading the Claude skill of challenge 71", e);
      return FILE_MOUNT_ERROR;
    } catch (IllegalArgumentException e) {
      log.warn("The upload token in the Claude skill of challenge 71 is not valid base64", e);
      return FILE_MOUNT_ERROR;
    }
  }
}
