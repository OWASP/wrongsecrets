package org.owasp.wrongsecrets.ctftests;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.WrongSecretsApplication;
import org.owasp.wrongsecrets.challenges.docker.Challenge1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {"K8S_ENV=docker", "ctf_enabled=true", "ctf_key=randomtextforkey"},
    classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class ChallengesControllerCTFModeTest {

  @Autowired private MockMvc mvc;

  @Test
  void shouldNotSpoilWhenInCTFMode() throws Exception {
    mvc.perform(get("/spoil/challenge-1"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Spoils are disabled in CTF mode")));
  }

  @Test
  void shouldNotSpoilWhenInCTFModeEvenWhenChallengeUnsupported() throws Exception {
    mvc.perform(get("/spoil/challenge-5"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Spoils are disabled in CTF mode")));
  }

  @Test
  void challenge0SshouldNotShowTheAddressRightAnswersNeedToBeSsubmittedTo() throws Exception {
    mvc.perform(get("/challenge/challenge-0"))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("https://www.google.nl"))));
  }

  @Test
  void shouldShowFlagWhenRespondingWithSuccessInCTFMode() throws Exception {
    var spoil = new Challenge1().spoiler().solution();
    mvc.perform(
            post("/challenge/challenge-1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("ba9a72ac7057576344856")));
  }

  @Test
  void shouldNotEnableK8sExercises() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("challenge-5_disabled-link")))
        .andExpect(content().string(containsString("challenge-6_disabled-link")))
        .andExpect(content().string(containsString("challenge-7_disabled-link")));
  }

  @Test
  void shouldStillDissableTestsIfNotPreconfigured() throws Exception {
    testK8sChallenge("/challenge/challenge-5");
    testK8sChallenge("/challenge/challenge-6");
    testK8sChallenge("/challenge/challenge-7");
    testForCloudCluster("/challenge/challenge-9");
    testForCloudCluster("/challenge/challenge-10");
    testForCloudCluster("/challenge/challenge-11");
  }

  private void testK8sChallenge(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("We are running outside a K8s cluster")));
  }

  private void testForCloudCluster(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string(
                    containsString(
                        "We are running outside a properly configured Cloud environment.")));
  }
}
