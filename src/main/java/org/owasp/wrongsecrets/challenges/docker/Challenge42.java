package org.owasp.wrongsecrets.challenges.docker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.AuditConfiguration;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.Spoiler;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** This is a challenge based on finding API Key in Spring Boot Actuator audit events */
@Slf4j
@Order(42)
@Component
@RequiredArgsConstructor
public class Challenge42 implements Challenge {

  private final AuditConfiguration auditConfiguration;

  @Override
  public Spoiler spoiler() {
    return new Spoiler(auditConfiguration.getApiKey());
  }

  @Override
  public boolean answerCorrect(String answer) {
    return auditConfiguration.getApiKey().equals(answer);
  }
}
