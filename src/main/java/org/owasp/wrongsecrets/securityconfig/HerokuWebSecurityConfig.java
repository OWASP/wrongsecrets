package org.owasp.wrongsecrets.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/** Used to implement https redirect for our Heroku-hosted workload. */
@Configuration
public class HerokuWebSecurityConfig {

  @Bean
  @Order(2)
  public SecurityFilterChain configureHerokuWebSecurity(HttpSecurity http) throws Exception {
    http.requiresChannel()
        .requestMatchers(
            r ->
                r.getRequestURL().toString().contains("heroku")
                    && (r.getHeader("x-forwarded-proto") != null
                        || r.getHeader("X-Forwarded-Proto") != null))
        .requiresSecure();
    return http.build();
  }
}
