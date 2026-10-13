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
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(auth -> auth
                // Whitelists local project static directory assets and hard-drive upload streams publicly
                .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/webfonts/**", "/uploads/**", "/favicon.ico").permitAll()
                .anyRequest().permitAll()
            )
            
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                // 🟢 FIXED: Added wildcards (https://*.domain) to properly whitelist Content Delivery Networks (CDNs)
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://*.tailwindcss.com https://*.jsdelivr.net; " +
                    "style-src 'self' 'unsafe-inline' https://*.cloudflare.com https://*.googleapis.com; " +
                    "font-src 'self' data: https://*.cloudflare.com https://*.gstatic.com; " +
                    "img-src 'self' data: https://*;"
                ))
            );
            
        return http.build();
    }
}
