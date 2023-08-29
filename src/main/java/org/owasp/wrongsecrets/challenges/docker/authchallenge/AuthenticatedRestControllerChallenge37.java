package org.owasp.wrongsecrets.challenges.docker.authchallenge;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.docker.Challenge37;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthenticatedRestControllerChallenge37 {

  private final Challenge37 challenge37;

  public AuthenticatedRestControllerChallenge37(Challenge37 challenge37) {
    this.challenge37 = challenge37;
  }

  @Operation(summary = "Endpoint for interaction at challenge 37")
  @GetMapping("/authenticated/challenge37")
  public String getAuthSecret() {
    return challenge37.spoiler().solution();
  }
}
