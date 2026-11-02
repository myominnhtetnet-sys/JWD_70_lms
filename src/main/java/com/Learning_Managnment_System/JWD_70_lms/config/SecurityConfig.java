package com.Learning_Managnment_System.JWD_70_lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // 🟢 Import this

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/assets/**", 
                    "/webfonts/**", "/uploads/**", "/favicon.ico",
                    "/student/awards/download/**", "/verify-certificate"
                ).permitAll()
                .anyRequest().permitAll()
            )
            
            // 🟢 Add Spring Security Logout configuration here
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // Allows GET requests from your <a> tag
                .logoutSuccessUrl("/") // Redirects to http://localhost:8080/ after successful logout
                .invalidateHttpSession(true) // Clears the HttpSession
                .clearAuthentication(true)
                .permitAll()
            )
            
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://*.tailwindcss.com https://*.jsdelivr.net; " +
                    "script-src-elem 'self' 'unsafe-inline' https://*.tailwindcss.com https://*.jsdelivr.net; " +
                    "style-src 'self' 'unsafe-inline' https://*.cloudflare.com https://*.googleapis.com https://*.jsdelivr.net; " +
                    "font-src 'self' data: https://*.cloudflare.com https://*.gstatic.com; " +
                    "img-src 'self' data: https://*; " +
                    "connect-src 'self' https://*.jsdelivr.net; " +
                    "media-src 'self' https://*.youtube.com https://*.vimeo.com https://*; " +
                    "frame-src 'self' https://*.youtube.com https://*.vimeo.com; " +
                    "worker-src 'self' blob:;"
                ))
            );
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {   	
        return new BCryptPasswordEncoder();
    }
}