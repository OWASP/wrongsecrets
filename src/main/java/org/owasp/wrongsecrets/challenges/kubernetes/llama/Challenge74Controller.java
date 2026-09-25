package org.owasp.wrongsecrets.challenges.kubernetes.llama;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/challenge/74")
public class Challenge74Controller {

  private final Challenge74 challenge;

  public Challenge74Controller(Challenge74 challenge) {
    this.challenge = challenge;
  }

  @PostMapping("/chat")
  public ChatResponse chat(@RequestBody ChatRequest request) {
    return new ChatResponse(challenge.ask(request.message()));
  }

  public record ChatRequest(String message) {}

  public record ChatResponse(String response) {}
}
