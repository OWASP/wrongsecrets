package org.owasp.wrongsecrets;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {"hints_enabled=false"},
    classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class ChallengeAPIControllerHintsDisabledTest {

  @Autowired private MockMvc mvc;

  @Test
  void shouldReturnEmptyHintsListWhenHintsDisabled() throws Exception {
    mvc.perform(get("/api/Hints"))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("ChallengeId"))))
        .andExpect(content().string(containsString("\"data\":[]")));
  }
}
