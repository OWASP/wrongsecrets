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
 * Challenge based on a secret leaked inside a real AI coding-agent transcript. The transcript
 * captures a Codex session where the agent reads a staging configuration file containing a deploy
 * token, exposing it in the session output. This illustrates how AI coding-agent transcripts can
 * inadvertently retain sensitive information that may later be committed, shared, or exposed.
 */
@Slf4j
@Component
public class Challenge72 extends FixedAnswerChallenge {

  private static final Pattern DEPLOY_TOKEN_PATTERN =
      Pattern.compile("DEPLOY_TOKEN=([A-Za-z0-9_]+)");

  private final Resource transcriptFile;

  public Challenge72(
      @Value("classpath:challenges/challenge-72/codex-session-transcript.md")
          Resource transcriptFile) {
    this.transcriptFile = transcriptFile;
  }

  @Override
  public String getAnswer() {
    try {
      var transcriptContent = transcriptFile.getContentAsString(StandardCharsets.UTF_8);
      var matcher = DEPLOY_TOKEN_PATTERN.matcher(transcriptContent);
      if (!matcher.find()) {
        log.warn("Could not find the deploy token in the Codex transcript of challenge 72");
        return FILE_MOUNT_ERROR;
      }
      return matcher.group(1);
    } catch (IOException e) {
      log.warn("Exception while reading the Codex transcript of challenge 72", e);
      return FILE_MOUNT_ERROR;
    }
  }
}
