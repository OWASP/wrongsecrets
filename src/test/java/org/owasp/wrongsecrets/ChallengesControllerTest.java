package org.owasp.wrongsecrets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {"K8S_ENV=DOCKER"},
    classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class ChallengesControllerTest {

  @Autowired private MockMvc mvc;

  @Test
  void startingChallengeShouldClearCorrectOrIncorrectMessage() throws Exception {
    this.mvc
        .perform(
            post("/challenge/challenge-0")
                .param("solution", "wrong")
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(model().attributeDoesNotExist("answerCorrect"))
        .andExpect(model().attributeExists("answerIncorrect"));
  }

  @Test
  void shouldReturnSpoiler() throws Exception {
    this.mvc
        .perform(get("/spoil/challenge-0"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("spoiler", new Spoiler("The first answer")));
  }

  @Test
  void shouldReturnNotFound() throws Exception {
    this.mvc.perform(get("/challenge/-1")).andExpect(status().isNotFound());
    this.mvc.perform(get("/challenge/99999999")).andExpect(status().isNotFound());
  }
}
