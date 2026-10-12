package com.Learning_Managnment_System.JWD_70_lms.config;

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
            // 1. Disable CSRF to allow your raw HTML Thymeleaf POST forms to submit data safely
            .csrf(csrf -> csrf.disable())
            
            // 2. 🟢 THE FIX: Permitt all web request routing entry points so your custom handlers function
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            
            // 3. 🟢 THE FIX: Explicitly turn off Spring Security's built-in default form login overlay
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // 4. Keep your iframe options unblocked so YouTube videos play cleanly without connection errors
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );
            
        return http.build();
    }
}
