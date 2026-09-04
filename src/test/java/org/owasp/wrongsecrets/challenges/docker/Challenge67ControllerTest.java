package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

class Challenge67ControllerTest {

  private static final String SKILL_LOCATION =
      "challenges/challenge-67/cursor-skill/deploy-preview/SKILL.md";

  @Test
  void shouldServeTheCursorSkillAsMarkdown() {
    var controller = new Challenge67Controller(new ClassPathResource(SKILL_LOCATION));

    var response = controller.cursorSkill();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).hasToString("text/markdown;charset=UTF-8");
    assertThat(response.getBody()).contains("name: deploy-preview", "STAGING_DEPLOY_TOKEN=\"");
  }

  @Test
  void servedSkillShouldContainTheAnswerOfTheChallenge() {
    var controller = new Challenge67Controller(new ClassPathResource(SKILL_LOCATION));
    var challenge = new Challenge67(new ClassPathResource(SKILL_LOCATION));

    assertThat(controller.cursorSkill().getBody()).contains(challenge.spoiler().solution());
  }

  @Test
  void shouldReturnServerErrorWhenTheSkillIsMissing() {
    var controller =
        new Challenge67Controller(new ClassPathResource("challenges/challenge-67/missing.md"));

    assertThat(controller.cursorSkill().getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
