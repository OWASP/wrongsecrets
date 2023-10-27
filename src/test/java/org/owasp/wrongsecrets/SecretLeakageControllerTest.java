package org.owasp.wrongsecrets;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.docker.WrongSecretsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(properties = {"K8S_ENV=docker"})
@AutoConfigureMockMvc
class SecretLeakageControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void spoil1() throws Exception {
    testSpoil("/spoil/challenge-1", WrongSecretsConstants.password);
  }

  @Test
  void solveChallenge1() throws Exception {
    solveChallenge("/challenge/challenge-1", WrongSecretsConstants.password);
  }

  private void solveChallenge(String endpoint, String solution) throws Exception {
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(endpoint)
                .param("solution", solution)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Your answer is correct!")));
  }

  private void testSpoil(String endpoint, String solution) throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(endpoint))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(solution)));
  }
}
