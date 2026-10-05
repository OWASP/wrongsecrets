package org.owasp.wrongsecrets.ctftests;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.WrongSecretsApplication;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.cloud.Challenge67;
import org.owasp.wrongsecrets.challenges.docker.Challenge8;
import org.owasp.wrongsecrets.challenges.docker.authchallenge.Challenge37;
import org.owasp.wrongsecrets.challenges.docker.challenge30.Challenge30;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Verifies that challenges 8, 30, 37 and 67 each use their own CTF answer configuration while CTF
 * mode is enabled against a CTF-server: challenge 37 used to check the configuration of challenge
 * 30, which hid its answer as long as challenge 30 kept its default (see issue 2717).
 */
@SpringBootTest(
    properties = {
      "K8S_ENV=gcp",
      "ctf_enabled=true",
      "ctf_key=randomtextforkey",
      "CTF_SERVER_ADDRESS=https://ctf.example.org",
      "challenge_acht_ctf_to_provide_to_host_value=flag-for-challenge-8",
      "challenge_thirty_ctf_to_provide_to_host_value=not_set",
      "challenge_rando_key_ctf_to_provide_to_host_value=flag-for-challenge-37",
      "challenge_sixty_seven_ctf_to_provide_to_host_value=flag-for-challenge-67"
    },
    classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class ChallengesControllerCTFPerChallengeAnswersTest {

  @Autowired private MockMvc mvc;
  @Autowired private Challenge8 challenge8;
  @Autowired private Challenge30 challenge30;
  @Autowired private Challenge37 challenge37;
  @Autowired private Challenge67 challenge67;

  @Test
  void challenge37ShouldShowItsOwnAnswerWhileChallenge30KeepsTheDefault() throws Exception {
    solveChallenge("challenge-37", challenge37)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("flag-for-challenge-37")));
  }

  @Test
  void challenge30ShouldNotShowAnAnswerWhileItsOwnConfigurationKeepsTheDefault() throws Exception {
    solveChallenge("challenge-30", challenge30)
        .andExpect(status().isOk())
        .andExpect(model().attributeDoesNotExist("answerCorrect"))
        .andExpect(content().string(not(containsString("flag-for-challenge-8"))))
        .andExpect(content().string(not(containsString("flag-for-challenge-37"))))
        .andExpect(content().string(not(containsString("flag-for-challenge-67"))));
  }

  @Test
  void challenge8ShouldShowItsOwnConfiguredAnswer() throws Exception {
    solveChallenge("challenge-8", challenge8)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("flag-for-challenge-8")))
        .andExpect(content().string(not(containsString("flag-for-challenge-37"))));
  }

  @Test
  void challenge67ShouldShowItsOwnConfiguredAnswer() throws Exception {
    solveChallenge("challenge-67", challenge67)
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("flag-for-challenge-67")))
        .andExpect(content().string(not(containsString("flag-for-challenge-8"))));
  }

  private ResultActions solveChallenge(String shortName, Challenge challenge) throws Exception {
    return mvc.perform(
        post("/challenge/%s".formatted(shortName))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("solution", challenge.spoiler().solution())
            .param("action", "submit")
            .with(csrf()));
  }
}
