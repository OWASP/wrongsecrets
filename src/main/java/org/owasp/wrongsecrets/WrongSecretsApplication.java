package org.owasp.wrongsecrets;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.owasp.wrongsecrets.challenges.Challenge;
import org.owasp.wrongsecrets.challenges.kubernetes.Vaultpassword;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@SpringBootApplication
@EnableConfigurationProperties(Vaultpassword.class)
@Slf4j
public class WrongSecretsApplication {

  public static void main(String[] args) {
    SpringApplication.run(WrongSecretsApplication.class, args);
  }

  @Bean
  @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public InMemoryScoreCard scoreCard(List<Challenge> challenges) {
    log.info("Initializing scorecard with {} challenges", challenges.size());
    return new InMemoryScoreCard(challenges.size());
  }
}
