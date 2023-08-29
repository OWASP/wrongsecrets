package org.owasp.wrongsecrets.challenges.docker.authchallenge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/* Ensures the need for basic auth for the endpoint having the actual Secret */

@Configuration
public class AuthenticationChallengeSecurityConfig {

  @Bean
  @Order(0)
  public SecurityFilterChain configureBasicAuthForChallenge(HttpSecurity http) throws Exception {
    return http.authorizeRequests()
        .requestMatchers("/authenticated/**")
        .authenticated()
        .and()
        .httpBasic(Customizer.withDefaults())
        .build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails admin =
        User.builder().username("admin").password("{noop}admin").roles("ADMIN").build();
    return new InMemoryUserDetailsManager(admin);
  }
}
