package org.owasp.wrongsecrets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootTest(
    properties = {"K8S_ENV=DOCKER"},
    classes = WrongSecretsApplication.class)
@AutoConfigureMockMvc
class LanguageToggleTest {

  static {
    // Set default locale to English for all tests
    Locale.setDefault(Locale.ENGLISH);
  }

  @BeforeEach
  void ensureLocaleIsEnglish() {
    Locale.setDefault(Locale.ENGLISH);
  }

  @Autowired private MockMvc mvc;

  private String performAndGetBody(RequestBuilder request) throws Exception {
    return mvc.perform(request)
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString(StandardCharsets.UTF_8);
  }

  @Test
  void langParameterSwitchesUiToGerman() throws Exception {
    String body = performAndGetBody(get("/challenge/challenge-0").param("lang", "de"));

    assertThat(body).contains("data-cy=\"language-selector\"");
    assertThat(body).contains("Deine Aufgabe");
    assertThat(body).contains("Gib das gefundene Geheimnis ein:");
  }

  @Test
  void langParameterSwitchesChallengeContentToDutch() throws Exception {
    String body = performAndGetBody(get("/challenge/challenge-0").param("lang", "nl"));

    assertThat(body).contains("data-cy=\"language-selector\"");
    assertThat(body).contains("Eerste uitdaging");
    assertThat(body).doesNotContain("Opening Challenge");
  }

  @Test
  void languagePersistsInSessionWithoutLangParameter() throws Exception {
    MockHttpSession session = new MockHttpSession();
    performAndGetBody(get("/challenge/challenge-0").param("lang", "de").session(session));

    String body = performAndGetBody(get("/challenge/challenge-0").session(session));

    assertThat(body).contains("Deine Aufgabe");
    assertThat(body).contains("Gib das gefundene Geheimnis ein:");
  }

  @Test
  void freshSessionDefaultsToEnglish() throws Exception {
    // Explicitly create a new session to ensure we start fresh
    MockHttpSession freshSession = new MockHttpSession();
    // Explicitly set locale to English in the session using the Spring constant
    freshSession.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, Locale.ENGLISH);
    String body = performAndGetBody(get("/challenge/challenge-0").session(freshSession));

    assertThat(body).contains("Your Task");
    assertThat(body).contains("Opening Challenge");
    assertThat(body).doesNotContain("Deine Aufgabe");
    assertThat(body).doesNotContain("Gib das gefundene Geheimnis ein:");
  }
}
