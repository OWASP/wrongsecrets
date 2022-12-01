package org.owasp.wrongsecrets.canaries;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class TokenCallbackSecurityConfiguration {

    @Bean
    @Order(0)
    public DefaultSecurityFilterChain configureTokenCallbackSecurity(HttpSecurity http) throws Exception {
        http.requestMatcher(r ->
                r.getRequestURL().toString().contains("canaries") || r.getRequestURL().toString().contains("token"))
            .csrf().disable();
        return http.build();
    }
}
