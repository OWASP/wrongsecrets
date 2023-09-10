package org.owasp.wrongsecrets.challenges.docker.authchallenge;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.owasp.wrongsecrets.challenges.docker.Challenge37;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedRestControllerChallenge37Test {

  private MockMvc mvc;

  @Mock private Challenge37 challenge;

  @BeforeEach
  void setup() {
    when(challenge.spoiler()).thenReturn(new Spoiler("solution"));
    var controller = new AuthenticatedRestControllerChallenge37(challenge);
    mvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  public void shouldNotAllowToGetAuthValues() throws Exception {
    this.mvc
        .perform(get("/authenticated/challenge37"))
        .andExpect(status().isOk()); // should not be the case!
  }
  // §todo: add tests for basic-auth: as AuthenticationChallengeSecurityConfig is not picked up

}
