package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Challenge based on a secret that is hardcoded in a Cursor skill. The skill is shipped as a plain
 * {@code SKILL.md} file, so the secret is readable for anybody who receives the skill.
 */
@Slf4j
@Component
public class Challenge67 extends FixedAnswerChallenge {

  private static final Pattern DEPLOY_TOKEN_PATTERN =
      Pattern.compile("STAGING_DEPLOY_TOKEN=\"([^\"]+)\"");

  private final Resource skillFile;

  public Challenge67(
      @Value("classpath:challenges/challenge-67/cursor-skill/deploy-preview/SKILL.md")
          Resource skillFile) {
    this.skillFile = skillFile;
  }

  @Override
  public String getAnswer() {
    try {
      var skillContent = skillFile.getContentAsString(StandardCharsets.UTF_8);
      var matcher = DEPLOY_TOKEN_PATTERN.matcher(skillContent);
      if (!matcher.find()) {
        log.warn("Could not find the deploy token in the Cursor skill of challenge 67");
        return FILE_MOUNT_ERROR;
      }
      return matcher.group(1);
    } catch (IOException e) {
      log.warn("Exception while reading the Cursor skill of challenge 67", e);
      return FILE_MOUNT_ERROR;
    }
  }
}
