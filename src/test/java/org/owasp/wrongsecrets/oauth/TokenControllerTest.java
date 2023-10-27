package org.owasp.wrongsecrets.oauth;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class TokenControllerTest {

  @Autowired MockMvc mvc;

  @Test
  void shouldGetToken() throws Exception {
    // When
    var response =
        mvc.perform(
            post("/token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(
                    "grant_type=client_credentials&client_id=WRONGSECRET_CLIENT_ID&client_secret=this"
                        + " is second test secret"));

    // Then
    response
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").exists())
        .andExpect(jsonPath("$.token_type").value("bearer"))
        .andExpect(jsonPath("$.expires_in").value(54321))
        .andExpect(jsonPath("$.scope").value("user_info"));
  }

  @Test
  void shouldNotGetToken() throws Exception {
    // When
    var response =
        mvc.perform(
            post("/token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(
                    "grant_type=client_credentials&client_id=WRONGSECRET_CLIENT_ID&client_secret=this"
                        + " wrong secret"));

    // Then
    response.andExpect(status().isUnauthorized());
  }
}
