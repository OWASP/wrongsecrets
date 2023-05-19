package org.owasp.wrongsecrets;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class PortConfiguration {
  @Bean
  @Order(1)
  public SecurityFilterChain configureHerokuWebSecurityTest(
      HttpSecurity http, ObjectProvider<PortMapper> portMapper) throws Exception {
    portMapper.ifAvailable(http.portMapper()::portMapper);
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
