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
            // 1. Disable CSRF to allow your custom HTML form POST requests to submit safely
            .csrf(csrf -> csrf.disable())
            
            // 2. 🟢 THE FIX: Permit everyone to access your CSS, JS, and Images static folders!
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**").permitAll() // Allow assets
                .anyRequest().permitAll() // Permit your custom routes to pass through as well
            )
            
            // 3. Explicitly turn off Spring Security's auto-generated overlay forms
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // 4. Keep your frame options set to sameOrigin so your YouTube frames connect cleanly
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );
            
        return http.build();
    }
}
