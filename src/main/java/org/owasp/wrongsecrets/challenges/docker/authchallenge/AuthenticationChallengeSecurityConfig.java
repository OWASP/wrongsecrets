package org.owasp.wrongsecrets.challenges.docker.authchallenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/* Ensures the need for basic auth for the endpoint having the actual Secret */

@Configuration
@EnableWebSecurity
public class AuthenticationChallengeSecurityConfig {

    @Autowired
    private MyBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser("user1")
            .password(passwordEncoder().encode("user1Pass"))
            .authorities("ROLE_USER");
    }
  @Bean
  @Order(3)
  public SecurityFilterChain configureBasicAuthForChallenge(HttpSecurity http) throws Exception {
      http.authorizeRequests()
          .requestMatchers("/authenticated")
          .permitAll()
          .anyRequest()
          .authenticated()
          .and()
          .httpBasic(new Customizer<HttpBasicConfigurer<HttpSecurity>>() {
              @Override
              public void customize(HttpBasicConfigurer<HttpSecurity> httpSecurityHttpBasicConfigurer) {
                  httpSecurityHttpBasicConfigurer.authenticationEntryPoint(authenticationEntryPoint);
              }
          });
      http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
      return http.build();
  }

    @Bean
    @Order(4)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
