package org.owasp.wrongsecrets;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain security(
      HttpSecurity http,
      ObjectProvider<PortMapper> portMapperProvider,
      List<BasicAuthentication> auths)
      throws Exception {
    configureHerokuHttps(http, portMapperProvider.getIfAvailable(PortMapperImpl::new));
    configureBasicAuthentication(http, auths);
    configureCsrf(http);
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService(List<BasicAuthentication> auths) {
    var users =
        auths.stream()
            .map(
                auth ->
                    User.builder()
                        .username(auth.username())
                        .password("{noop}" + auth.password())
                        .roles(auth.role())
                        .build())
            .toList();
    return new InMemoryUserDetailsManager(users);
  }

  private void configureCsrf(HttpSecurity http) throws Exception {
    http.csrf(
        csrf ->
            csrf.ignoringRequestMatchers(
                "/canaries/tokencallback", "/canaries/tokencallbackdebug", "/token"));
  }

  private void configureBasicAuthentication(HttpSecurity http, List<BasicAuthentication> auths)
      throws Exception {
    var patterns = auths.stream().map(auth -> auth.urlPattern()).toList();
    http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(patterns.toArray(new String[patterns.size()]))
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .httpBasic(Customizer.withDefaults());
  }

  private void configureHerokuHttps(HttpSecurity http, PortMapper portMapper) throws Exception {
    var requestMatcher =
        new RequestMatcher() {
          @Override
          public boolean matches(HttpServletRequest request) {
            return request.getRequestURL().toString().contains("heroku")
                && request.getHeader("x-forwarded-proto") != null;
          }
        };
    http.requiresChannel(channel -> channel.requestMatchers(requestMatcher).requiresSecure())
        .portMapper(configurer -> configurer.portMapper(portMapper));
  }
}
