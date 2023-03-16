package org.owasp.wrongsecrets.canaries;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TokenCallbackSecurityConfiguration {

    @SuppressFBWarnings(value = "SPRING_CSRF_PROTECTION_DISABLED",justification = "There is no need for the token & canaries endpoints to have CSRF as it is only used for callbacks by canarytokens.org")
    @Bean
    @Order(0)
    public SecurityFilterChain configureTokenCallbackSecurity(HttpSecurity http) throws Exception {
        http.securityMatcher(r ->
                r.getRequestURL().toString().contains("canaries") || r.getRequestURL().toString().contains("token"))
            .csrf().disable();
        return http.build();
    }
}
