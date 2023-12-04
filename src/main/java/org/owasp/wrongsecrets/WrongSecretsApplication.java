package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.challenges.kubernetes.Vaultpassword;
import org.owasp.wrongsecrets.definitions.ChallengeDefinitionsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@SpringBootApplication
@EnableConfigurationProperties(Vaultpassword.class)
public class WrongSecretsApplication {

  public static void main(String[] args) {
    SpringApplication.run(WrongSecretsApplication.class, args);
  }

  @Bean
  @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public InMemoryScoreCard scoreCard(Challenges challenges) {
    return new InMemoryScoreCard(challenges);
  }

  @Bean
  public RuntimeEnvironment runtimeEnvironment(
      @Value("${K8S_ENV}") String currentRuntimeEnvironment,
      ChallengeDefinitionsConfiguration challengeDefinitions) {
    return RuntimeEnvironment.fromString(currentRuntimeEnvironment, challengeDefinitions);
  }
}
