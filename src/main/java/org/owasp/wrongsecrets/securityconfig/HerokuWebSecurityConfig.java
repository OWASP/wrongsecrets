package org.owasp.wrongsecrets.securityconfig;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Used to implement https redirect for our Heroku-hosted workload.
 */
@Configuration
public class HerokuWebSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain configureHerokuWebSecurity(HttpSecurity http, ObjectProvider<PortMapper> portMapper) throws Exception {
        http.requiresChannel()
            .requestMatchers(r ->
                r.getRequestURL().toString().contains("heroku")
                    && (r.getHeader("x-forwarded-proto") != null || r.getHeader("X-Forwarded-Proto") != null)
            )
            .requiresSecure();
        return http.build();
    }
}
