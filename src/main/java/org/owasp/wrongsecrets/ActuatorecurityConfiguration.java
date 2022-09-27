package org.owasp.wrongsecrets;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@Order(2)
public class ActuatorecurityConfiguration extends HerokuWebSecurityConfig {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatcher(r ->
                r.getRequestURL().toString().contains("/actuator/health"))
            .csrf().disable();
    }
}
