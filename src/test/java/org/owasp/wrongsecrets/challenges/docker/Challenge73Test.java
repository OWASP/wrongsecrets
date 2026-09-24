package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class Challenge73Test {

  private static final String DEFAULT_AGENT_CONTEXT_PATH = "./.devcontainer";
  private static final String EXPECTED_TOKEN = "Ag3nt_C0nt3xt_L3ak3d_T0k3n!";

  private static final List<String> AGENT_CONTEXT_FILES =
      List.of("AGENTS.md", "CLAUDE.md", ".cursor/rules/project.mdc", ".windsurfrules");

  private static void writeAgentContextFiles(Path dir, String token) throws IOException {
    for (var fileName : AGENT_CONTEXT_FILES) {
      var file = dir.resolve(fileName);
      Files.createDirectories(file.getParent());
      Files.writeString(file, "# " + fileName + "\n\nNo secrets here.\n");
    }
    Files.writeString(
        dir.resolve("CLAUDE.md"), "# CLAUDE.md\n\nexport STAGING_DEPLOY_TOKEN=\"" + token + "\"\n");
  }

  @Test
  void spoilerShouldGiveTheTokenShippedWithTheDevContainer() {
    var challenge = new Challenge73(DEFAULT_AGENT_CONTEXT_PATH);

    assertThat(challenge.spoiler().solution()).isEqualTo(EXPECTED_TOKEN);
    assertThat(challenge.answerCorrect(EXPECTED_TOKEN)).isTrue();
  }

  @Test
  void shippedAgentContextFilesShouldContainTheTokenOnlyOnce() throws IOException {
    var occurrences = 0;
    for (var fileName : AGENT_CONTEXT_FILES) {
      var content =
          Files.readString(
              Path.of(DEFAULT_AGENT_CONTEXT_PATH).resolve(fileName), StandardCharsets.UTF_8);
      occurrences += content.split(EXPECTED_TOKEN, -1).length - 1;
    }

    assertThat(occurrences).isEqualTo(1);
  }

  @Test
  void shouldExtractTheTokenFromTheAgentContextFiles(@TempDir Path dir) throws IOException {
    writeAgentContextFiles(dir, "t0k3n-from-the-agent-context");

    var challenge = new Challenge73(dir.toString());

    assertThat(challenge.spoiler().solution()).isEqualTo("t0k3n-from-the-agent-context");
    assertThat(challenge.answerCorrect("t0k3n-from-the-agent-context")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge73(DEFAULT_AGENT_CONTEXT_PATH);

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
  }

  @Test
  void shouldReportAnErrorWhenTheAgentContextIsMissing(@TempDir Path dir) {
    var challenge = new Challenge73(dir.resolve("does-not-exist").toString());

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }
}
