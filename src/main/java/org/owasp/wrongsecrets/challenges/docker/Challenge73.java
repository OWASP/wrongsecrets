package org.owasp.wrongsecrets.challenges.docker;

import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.FixedAnswerChallenge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Challenge about secrets that are committed to AI agent instruction/context files. The dev
 * container ships {@code AGENTS.md}, {@code CLAUDE.md}, {@code .cursor/rules/project.mdc} and
 * {@code .windsurfrules}; one of them inlines a shared credential that every agent (and every
 * reader of the repository) picks up.
 */
@Slf4j
@Component
public class Challenge73 extends FixedAnswerChallenge {

  private static final Pattern TOKEN_PATTERN = Pattern.compile("STAGING_DEPLOY_TOKEN=\"([^\"]+)\"");

  private static final List<String> AGENT_CONTEXT_FILES =
      List.of("AGENTS.md", "CLAUDE.md", ".cursor/rules/project.mdc", ".windsurfrules");

  private final String agentContextPath;

  /**
   * Constructor for creating a new Challenge73 object.
   *
   * @param agentContextPath directory in the dev container that holds the agent instruction files.
   */
  public Challenge73(@Value("${AGENT_CONTEXT_PATH}") String agentContextPath) {
    this.agentContextPath = agentContextPath;
  }

  @Override
  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "The path is a configured location of the agent context files.")
  public String getAnswer() {
    var basePath = Path.of(agentContextPath);
    for (var fileName : AGENT_CONTEXT_FILES) {
      var file = basePath.resolve(fileName);
      try {
        var content = Files.readString(file, StandardCharsets.UTF_8);
        var matcher = TOKEN_PATTERN.matcher(content);
        if (matcher.find()) {
          return matcher.group(1);
        }
      } catch (IOException e) {
        log.warn("Could not read agent context file {} of challenge 73", file, e);
      }
    }
    log.warn("Could not find the staging token in the agent context files of challenge 73");
    return FILE_MOUNT_ERROR;
  }
}
