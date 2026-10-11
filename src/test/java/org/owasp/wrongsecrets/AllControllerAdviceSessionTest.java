package org.owasp.wrongsecrets;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.owasp.wrongsecrets.challenges.docker.SlackNotificationService;
import org.owasp.wrongsecrets.challenges.docker.WrongSecretsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "K8S_ENV=docker")
@AutoConfigureMockMvc
class AllControllerAdviceSessionTest {

  @Autowired private MockMvc mvc;
  @MockitoBean private SlackNotificationService slackNotificationService;

  @Test
  void cachedChallengeListKeepsSessionsSeparate() throws Exception {
    var firstSession = new MockHttpSession();
    var secondSession = new MockHttpSession();

    mvc.perform(get("/").session(firstSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("data-cy=\"challenge-1-link\"")));
    mvc.perform(get("/").session(secondSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("data-cy=\"challenge-1-link\"")));

    mvc.perform(
            post("/challenge/challenge-1")
                .session(firstSession)
                .param("solution", WrongSecretsConstants.password)
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk());

    mvc.perform(get("/").session(firstSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("data-cy=\"challenge-1_completed-link\"")));
    mvc.perform(get("/").session(secondSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("data-cy=\"challenge-1-link\"")))
        .andExpect(content().string(not(containsString("data-cy=\"challenge-1_completed-link\""))));
    mvc.perform(get("/").session(firstSession))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("data-cy=\"challenge-1_completed-link\"")));
  }
}
