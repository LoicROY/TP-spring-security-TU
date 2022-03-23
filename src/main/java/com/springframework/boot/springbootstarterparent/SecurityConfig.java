package com.springframework.boot.springbootstarterparent;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/dashboard").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/tasks/**").authenticated()
                .mvcMatchers("/api/users/**").permitAll()
                .mvcMatchers("/**").authenticated()
                .and()
                .httpBasic();
    }
}
