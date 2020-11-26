package com.myManage.config;


import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                .clearAuthentication(Boolean.TRUE)
                .invalidateHttpSession(Boolean.TRUE)
                .permitAll()
        .and()
                .csrf()
                .disable()
                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/logout");
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}

