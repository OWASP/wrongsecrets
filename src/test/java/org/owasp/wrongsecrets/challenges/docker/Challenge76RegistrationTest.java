package org.owasp.wrongsecrets.challenges.docker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.Challenges;
import org.owasp.wrongsecrets.WrongSecretsApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {"K8S_ENV=DOCKER"},
    classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class Challenge76RegistrationTest {

  @Autowired private Challenges challenges;

  @Autowired private MockMvc mvc;

  @Test
  void challenge76ShouldBeRegisteredAndDiscoverable() {
    var definition = challenges.findByShortName("challenge-76");

    assertThat(definition).isPresent();
    assertThat(challenges.getChallenge(definition.get())).hasSize(1);
    assertThat(challenges.getChallenge(definition.get()).getFirst())
        .isInstanceOf(Challenge76.class);
  }

  @Test
  void challengePageShouldDisplayTheSelectedTargetUsername() throws Exception {
    var challenge = challenge76();

    mvc.perform(get("/challenge/challenge-76"))
        .andExpect(status().isOk())
        .andExpect(content().string(Matchers.containsString(challenge.getTargetUsername())));
  }

  @Test
  void spoilShouldContainThePasswordOfTheDisplayedTargetUser() throws Exception {
    var challenge = challenge76();

    mvc.perform(get("/spoil/challenge-76"))
        .andExpect(status().isOk())
        .andExpect(content().string(Matchers.containsString(challenge.spoiler().solution())));

    assertThat(challenge.answerCorrect(challenge.spoiler().solution())).isTrue();
    assertThat(challenge.answerCorrect("not-the-password-of-the-target-user")).isFalse();
  }

  private Challenge76 challenge76() {
    var definition = challenges.findByShortName("challenge-76").orElseThrow();
    return (Challenge76) challenges.getChallenge(definition).getFirst();
  }
}
