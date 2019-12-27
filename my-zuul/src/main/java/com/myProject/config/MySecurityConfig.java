package com.myProject.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Author: menfeng
 * @Date: 2019/12/26 9:39
 * @Version 1.0
 */
@EnableOAuth2Sso
@Configuration
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/logout")
                .authenticated()
                .anyRequest()
                .authenticated()
        .and()
                .logout()
                .logoutSuccessUrl("/exit")
                .invalidateHttpSession(Boolean.TRUE)
                .clearAuthentication(Boolean.TRUE)
                .permitAll()
        .and()
                .csrf()
                .disable()
                .userDetailsService(userDetailsServiceBean());

}
    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return null;
    }
}
