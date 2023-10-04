package org.owasp.wrongsecrets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConventionPortMapper.class)
@AutoConfigureMockMvc
class SecurityConfigTest {

  @Autowired private MockMvc mvc;
  @Autowired private BasicAuthentication challenge37BasicAuth;

  @Test
  void shouldRedirectWhenProtoProvided() throws Exception {
    mvc.perform(get("/heroku").header("x-forwarded-proto", "value"))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void shouldNotRedirectWhenProtoNotProvided() throws Exception {
    mvc.perform(get("/")).andExpect(status().isOk());
  }

  @Test
  void shouldFailDueToMissingCsrfToken() throws Exception {
    mvc.perform(
            post("/challenge/5")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", "test")
                .param("action", "submit"))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldSucceedWhenCsrfTokenIsPresent() throws Exception {
    mvc.perform(
            post("/challenge/5")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("solution", "test")
                .param("action", "submit")
                .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  void shouldNotAllowToGetAuthValues() throws Exception {
    this.mvc.perform(get("/authenticated/challenge37")).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldAllowToGetAuthValuesWithBasicAuth() throws Exception {
    this.mvc
        .perform(
            get("/authenticated/challenge37")
                .with(httpBasic(challenge37BasicAuth.username(), challenge37BasicAuth.password())))
        .andExpect(status().isOk());
  }
}
