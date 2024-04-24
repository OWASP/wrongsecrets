package org.owasp.wrongsecrets.challenges.docker.authchallenge;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AuthenticatedRestControllerChallenge37 {

  private final Challenge37 challenge37;

  @Operation(summary = "Endpoint for interaction at challenge 37")
  @GetMapping("/authenticated/challenge37")
  public String getAuthSecret() {
    return challenge37.spoiler().solution();
  }
}
