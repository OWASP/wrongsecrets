package org.owasp.wrongsecrets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ActuatorSecurityConfiguration {

    @SuppressFBWarnings(value = "SPRING_CSRF_PROTECTION_DISABLED",justification = "There is no need for the health endpoint to have CSRF as it is only used by K8s")
    @Bean
    @Order(2)
    public SecurityFilterChain configureActuatorSecurity(HttpSecurity http) throws Exception {
        http.securityMatcher(r ->
                r.getRequestURL().toString().contains("/actuator/health"))
            .csrf().disable();
        return http.build();
    }
}
