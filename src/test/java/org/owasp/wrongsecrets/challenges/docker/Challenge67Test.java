package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.owasp.wrongsecrets.Challenges.ErrorResponses.FILE_MOUNT_ERROR;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class Challenge67Test {

  private static final String SKILL_LOCATION =
      "challenges/challenge-67/cursor-skill/deploy-preview/SKILL.md";

  private static Resource skillContaining(String content) {
    return new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void spoilerShouldGiveTheTokenFromTheShippedSkill() {
    var challenge = new Challenge67(new ClassPathResource(SKILL_LOCATION));

    assertThat(challenge.spoiler().solution()).isNotEmpty().isNotEqualTo(FILE_MOUNT_ERROR);
    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
  }

  @Test
  void shippedSkillShouldInlineTheTokenInsteadOfReadingItFromTheEnvironment() throws Exception {
    var skill = new ClassPathResource(SKILL_LOCATION).getContentAsString(StandardCharsets.UTF_8);

    assertThat(skill).contains("STAGING_DEPLOY_TOKEN=\"");
  }

  @Test
  void shouldExtractTheTokenFromTheSkillFile() {
    var challenge =
        new Challenge67(
            skillContaining(
                """
                ## Prerequisites

                ```bash
                export STAGING_DEPLOY_URL="https://staging.example.com/api/v1/deploy"
                export STAGING_DEPLOY_TOKEN="t0k3n-from-the-skill"
                ```
                """));

    assertThat(challenge.spoiler().solution()).isEqualTo("t0k3n-from-the-skill");
    assertThat(challenge.answerCorrect("t0k3n-from-the-skill")).isTrue();
  }

  @Test
  void incorrectAnswerShouldNotSolveChallenge() {
    var challenge = new Challenge67(new ClassPathResource(SKILL_LOCATION));

    assertThat(challenge.answerCorrect("wrong answer")).isFalse();
    assertThat(challenge.answerCorrect("")).isFalse();
  }

  @Test
  void shouldReportAnErrorWhenTheSkillHasNoToken() {
    var challenge = new Challenge67(skillContaining("# Deploy Preview\n\nNo secrets here.\n"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }

  @Test
  void shouldReportAnErrorWhenTheSkillCannotBeRead() {
    var challenge =
        new Challenge67(new ClassPathResource("challenges/challenge-67/does-not-exist.md"));

    assertThat(challenge.spoiler().solution()).isEqualTo(FILE_MOUNT_ERROR);
  }
}
