package org.owasp.wrongsecrets.canaries;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(0)
public class TokenCallbackSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatcher(r ->
                r.getRequestURL().toString().contains("canaries") || r.getRequestURL().toString().contains("token"))
            .csrf().disable();
    }
}
