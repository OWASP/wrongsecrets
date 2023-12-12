package org.owasp.wrongsecrets.challenges.cloud;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.WrongSecretsApplication;
import org.owasp.wrongsecrets.challenges.cloud.challenge11.Challenge11Aws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {
      "K8S_ENV=GCP",
      "ctf_key=randomtextforkey",
      "SPECIAL_K8S_SECRET=test5",
      "SPECIAL_SPECIAL_K8S_SECRET=test6",
      "vaultpassword=test7",
      "secretmountpath=nothere",
      "default_aws_value_challenge_9=ACTUAL_ANSWER_CHALLENGE9",
      "default_aws_value_challenge_10=ACTUAL_ANSWER_CHALLENGE10",
      "default_aws_value_challenge_11=ACTUAL_ANSWER_CHALLENGE_11"
    },
    classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class ChallengesControllerWithPresetCloudValuesTest {

  @Autowired private MockMvc mvc;
  @Autowired private Challenges challenges;
  @Autowired private Challenge11Aws challenge11;

  @Test
  void shouldSpoilExercises() throws Exception {
    var firstChallenge = challenges.getChallengeDefinitions().getFirst();
    mvc.perform(get("/spoil/%s".formatted(firstChallenge.name().shortName())))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("The first answer")));
    mvc.perform(get("/spoil/challenge-5"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("test5")));
    mvc.perform(get("/spoil/challenge-6"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("test6")));
    mvc.perform(get("/spoil/challenge-9"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("ACTUAL_ANSWER_CHALLENGE9")));
  }

  @Test
  void shouldNotShowDisabledChallenge11() throws Exception {
    var spoil = challenge11.spoiler().solution();

    mvc.perform(
            post("/challenge/challenge-11")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("This challenge has been disabled."))));
  }

  @Test
  void shouldEnableCloudExercises() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("challenge-9_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge-10_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge-11_disabled-link"))));
  }

  @Test
  void shouldEnableK8sExercises() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("challenge-5_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge-6_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge-7_disabled-link"))));
  }

  @Test
  void shouldEnableDockerExercises() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("challenge-1_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge-2_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge-22_disabled-link"))));
  }
}
