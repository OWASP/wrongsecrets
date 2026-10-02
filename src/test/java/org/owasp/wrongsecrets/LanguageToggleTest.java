package org.owasp.wrongsecrets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

@SpringBootTest(properties = {"K8S_ENV=DOCKER"}, classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class LanguageToggleTest {

  @Autowired private MockMvc mvc;

  private String performAndGetBody(RequestBuilder request) throws Exception {
    return mvc.perform(request)
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
  }

  @Test
  void langParameterSwitchesUiAndChallengeContentToGerman() throws Exception {
    String body = performAndGetBody(get("/challenge/challenge-0").param("lang", "de"));

    assertThat(body).contains("data-cy=\"language-selector\"");
    assertThat(body).contains("Deine Aufgabe");
    assertThat(body).contains("Die richtige Antwort lautet");
  }

  @Test
  void languagePersistsInSessionWithoutLangParameter() throws Exception {
    MockHttpSession session = new MockHttpSession();
    performAndGetBody(get("/challenge/challenge-0").param("lang", "de").session(session));

    String body = performAndGetBody(get("/challenge/challenge-0").session(session));

    assertThat(body).contains("Deine Aufgabe");
    assertThat(body).contains("Die richtige Antwort lautet");
  }

  @Test
  void freshSessionDefaultsToEnglish() throws Exception {
    String body = performAndGetBody(get("/challenge/challenge-0"));

    assertThat(body).contains("Your Task");
    assertThat(body).doesNotContain("Deine Aufgabe");
    assertThat(body).doesNotContain("Die richtige Antwort lautet");
  }
}
