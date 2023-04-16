package org.owasp.wrongsecrets.securityconfig;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration used to disable CSRF on the actuator health endpoint otherwise it does not work for health-checks.
 */
@Configuration
public class CacheSecurityConfiguration {

    @SuppressFBWarnings(value = "SPRING_CSRF_PROTECTION_DISABLED",justification = "There is no need for the health endpoint to have CSRF as it is only used by K8s")
    @Bean
    @Order(3)
    public SecurityFilterChain configureCachingAssetSecurity(HttpSecurity http) throws Exception {
        http.securityMatcher(r ->
                r.getRequestURL().toString().contains("css"))
            .headers().cacheControl().disable();
        http.securityMatcher(r ->
                r.getRequestURL().toString().contains("js"))
            .headers().cacheControl().disable();
        return http.build();
    }
}
