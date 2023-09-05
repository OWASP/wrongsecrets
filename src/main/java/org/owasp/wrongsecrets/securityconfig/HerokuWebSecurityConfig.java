package org.owasp.wrongsecrets.securityconfig;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.SecurityFilterChain;

/** Used to implement https redirect for our Heroku-hosted workload. */
@Configuration
@EnableWebSecurity
public class HerokuWebSecurityConfig {

  @Bean
  @Order(1)
  public SecurityFilterChain configureHerokuWebSecurity(
      HttpSecurity http, ObjectProvider<PortMapper> portMapperProvider) throws Exception {
    var portMapper = portMapperProvider.getIfAvailable();
    if (portMapper != null) {
      http.portMapper(configurer -> configurer.portMapper(portMapper));
    }
    http.securityMatcher(
            r ->
                r.getRequestURL().toString().contains("heroku")
                    && r.getHeader("x-forwarded-proto") != null)
        .requiresChannel((requiresChannel) -> requiresChannel.anyRequest().requiresSecure());
    return http.build();
  }
}
