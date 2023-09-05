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

/**
 * Ensures the need for basic auth for the endpoint having the actual Secret
 *
 * <p>Be careful with the order of the SecurityFilterChain beans. The first one that matches the
 * request will be used. You can see it in action if you put a breakpoint at {@link
 * org.springframework.security.web.FilterChainProxy#getFilters(javax.servlet.http.HttpServletRequest)}.
 */
@Configuration
public class AuthenticationChallengeSecurityConfig {

  @Bean
  @Order(3)
  public SecurityFilterChain configureBasicAuthForChallenge(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            r -> r.requestMatchers("/authenticated/**").authenticated().anyRequest().permitAll())
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
