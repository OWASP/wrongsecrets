package org.owasp.wrongsecrets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.owasp.wrongsecrets.challenges.docker.WrongSecretsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class SecretLeakageControllerTest {

  @Autowired private WebApplicationContext webApplicationContext;
  private MockMvc mockMvc;
  @MockBean VaultTemplate vaultTemplate;
  @MockBean RuntimeEnvironment runtimeEnvironment;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    when(runtimeEnvironment.getRuntimeEnvironment())
        .thenReturn(RuntimeEnvironment.Environment.DOCKER);
    when(runtimeEnvironment.canRun(ArgumentMatchers.any())).thenReturn(true);
  }

  @Test
  void spoil1() throws Exception {
    testSpoil("/spoil-1", WrongSecretsConstants.password);
  }

  @Test
  void solveChallenge1() throws Exception {
    solveChallenge("/challenge/1", WrongSecretsConstants.password);
  }

  private void solveChallenge(String endpoint, String solution) throws Exception {
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.post(endpoint)
                .param("solution", solution)
                .param("action", "submit"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content()
                .string(CoreMatchers.containsString("Your answer is correct!")));
  }

  private void testSpoil(String endpoint, String solution) throws Exception {
    this.mockMvc
        .perform(MockMvcRequestBuilders.get(endpoint))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.containsString(solution)));
  }
}
