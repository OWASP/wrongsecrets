package org.owasp.wrongsecrets.ctftests;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.owasp.wrongsecrets.InMemoryScoreCard;
import org.owasp.wrongsecrets.RuntimeEnvironment;
import org.owasp.wrongsecrets.WrongSecretsApplication;
import org.owasp.wrongsecrets.challenges.cloud.Challenge10;
import org.owasp.wrongsecrets.challenges.cloud.Challenge11;
import org.owasp.wrongsecrets.challenges.cloud.Challenge9;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    properties = {
      "ctf_enabled=true",
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
class ChallengesControllerCTFModeWithPresetCloudValuesTest {

  @Autowired private MockMvc mvc;

  @Test
  void shouldNotSpoilWhenInCTFMode() throws Exception {
    mvc.perform(get("/spoil-9"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Spoils are disabled in CTF mode")));
  }

  @Test
  void shouldShowFlagWhenRespondingWithSuccessInCTFModeChallenge9() throws Exception {
    var spoil =
        new Challenge9(
                new InMemoryScoreCard(1),
                null,
                "ACTUAL_ANSWER_CHALLENGE9",
                "wrongsecret",
                new RuntimeEnvironment(RuntimeEnvironment.Environment.HEROKU_DOCKER))
            .spoiler()
            .solution();
    mvc.perform(
            post("/challenge/9")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("70d75bf845890b2419bd8795c")));
  }

  @Test
  void shouldShowFlagWhenRespondingWithSuccessInCTFModeChallenge10() throws Exception {
    var spoil =
        new Challenge10(
                new InMemoryScoreCard(1),
                null,
                "ACTUAL_ANSWER_CHALLENGE10",
                "wrongsecret-2",
                new RuntimeEnvironment(RuntimeEnvironment.Environment.HEROKU_DOCKER))
            .spoiler()
            .solution();
    mvc.perform(
            post("/challenge/10")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("176e937a2cafea3b0da3")));
  }

  @Test
  void shouldNotShowFlagWhenRespondingWithSuccessInCTFModeChallenge11() throws Exception {
    var spoil =
        new Challenge11(
                new InMemoryScoreCard(1),
                "awsRoleArn",
                "tokenFileLocation",
                "awsRegion",
                "gcpDefualtValue",
                "awsDefaultValue",
                "azureDefaultValue",
                "azureVaultUri",
                "azureWrongSecret3",
                "projectId",
                "ACTUAL_ANSWER_CHALLENGE_11",
                true,
                new RuntimeEnvironment(RuntimeEnvironment.Environment.HEROKU_DOCKER))
            .spoiler()
            .solution();

    mvc.perform(
            post("/challenge/11")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", spoil)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("This challenge has been disabled.")));
  }

  @Test
  void shouldEnableCloudExerciseBut11() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("challenge 9_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge 10_disabled-link"))))
        .andExpect(content().string(containsString("challenge 11_disabled-link")));
  }

  @Test
  void shouldEnableK8sExercises() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("challenge 5_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge 6_disabled-link"))))
        .andExpect(content().string(not(containsString("challenge 7_disabled-link"))));
  }
}
