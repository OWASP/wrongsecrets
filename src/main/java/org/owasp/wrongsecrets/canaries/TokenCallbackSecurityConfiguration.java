package org.owasp.wrongsecrets.canaries;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity(debug = true)
@Order(1)
public class TokenCallbackSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().requestMatchers(r -> r.getRequestURL().toString().contains("canaries")).permitAll()
            .and().requestMatcher(r -> r.getRequestURL().toString().contains("canaries")).csrf().disable().httpBasic().disable().sessionManagement().disable();
    }
}
