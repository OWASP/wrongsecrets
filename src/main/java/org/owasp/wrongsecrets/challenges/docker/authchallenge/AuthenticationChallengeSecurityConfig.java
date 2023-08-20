package org.owasp.wrongsecrets.challenges.docker.authchallenge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/* Ensures the need for basic auth for the endpoint having the actual Secret */

@Configuration
public class AuthenticationChallengeSecurityConfig {

  @Bean
  @Order(2)
  public SecurityFilterChain configureBasicAuthForChallenge(HttpSecurity http) throws Exception {
    http.securityMatcher();
    return http.build();
  }
}
