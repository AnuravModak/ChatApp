package com.demo.chatApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/ws/**", "/js/**", "/css/**", "/img/**", "/", "/index.html", "/users", "/messages/**").permitAll() // Updated method
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()); // Updated CSRF disabling method

        return http.build();
    }
}