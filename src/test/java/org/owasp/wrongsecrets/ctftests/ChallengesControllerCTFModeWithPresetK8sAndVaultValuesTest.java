package org.owasp.wrongsecrets.ctftests;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.WrongSecretsApplication;
import org.owasp.wrongsecrets.challenges.kubernetes.Challenge5;
import org.owasp.wrongsecrets.challenges.kubernetes.Challenge6;
import org.owasp.wrongsecrets.challenges.kubernetes.Challenge7;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {
      "K8S_ENV=k8s_vault",
      "ctf_enabled=true",
      "ctf_key=randomtextforkey",
      "SPECIAL_K8S_SECRET=test5",
      "SPECIAL_SPECIAL_K8S_SECRET=test6",
      "vaultpassword=test7"
    },
    classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class ChallengesControllerCTFModeWithPresetK8sAndVaultValuesTest {

  @Autowired private MockMvc mvc;
  @Autowired private Challenge5 challenge5;
  @Autowired private Challenge6 challenge6;
  @Autowired private Challenge7 challenge7;

  @Test
  void shouldNotSpoilWhenInCTFMode() throws Exception {
    mvc.perform(get("/spoil/challenge-5"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Spoils are disabled in CTF mode")));
  }

  @Test
  void shouldShowFlagWhenRespondingWithSuccessInCTFModeChallenge5() throws Exception {
    var spoil = challenge5.spoiler().solution();
    mvc.perform(
            post("/challenge/challenge-5")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("26d5e409100ca8dc3bd2dba115b81f5b7889fbbd")));
  }

  @Test
  void shouldShowFlagWhenRespondingWithSuccessInCTFModeChallenge6() throws Exception {
    var spoil = challenge6.spoiler().solution();
    mvc.perform(
            post("/challenge/challenge-6")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("18af49a1b18359e0bf9b9a0")));
  }

  @Test
  void shouldShowFlagWhenRespondingWithSuccessInCTFModeChallenge7() throws Exception {
    var spoil = challenge7.spoiler().solution();
    mvc.perform(
            post("/challenge/challenge-7")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("881951b59ea4818c2")));
  }

  @Test
  void shouldEnableK8sAndVaultExercises() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("challenge 5_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge 6_disabled-link>"))))
        .andExpect(content().string(not(containsString("challenge 7_disabled-link"))));
  }
}
