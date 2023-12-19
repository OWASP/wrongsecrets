package org.owasp.wrongsecrets.challenges.docker.challenge42;

import groovy.util.logging.Slf4j;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@Slf4j
public class AuditConfiguration {

  private final String apiKey = UUID.randomUUID().toString();

  @Bean
  public InMemoryAuditEventRepository inMemoryAuditEventRepository() {
    InMemoryAuditEventRepository repository = new InMemoryAuditEventRepository();
    AuditEvent auditEvent =
        new AuditEvent("john.doe", "API_KEY_RECEIVED", Map.of("apiKey", apiKey));
    repository.add(auditEvent);
    return repository;
  }
}
