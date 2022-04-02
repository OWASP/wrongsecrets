package org.owasp.wrongsecrets;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity(debug = true)
public class HerokuWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requiresChannel()
            .requestMatchers(r -> r.getHeader("x-forwarded-proto") != null)
            .requiresSecure()
            .and()
            .httpBasic().disable();
        http.requestMatcher(r -> r.getRequestURI().contains("canaries/tokencallback"))
            .csrf().disable();
    }
}
