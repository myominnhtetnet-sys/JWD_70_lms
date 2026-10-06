package com.Learning_Managnment_System.JWD_70_lms.config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry) {

        String uploadPath =
                Paths.get("uploads/assignments")
                     .toAbsolutePath()
                     .normalize()
                     .toUri()
                     .toString();

        registry.addResourceHandler(
                "/uploads/assignments/**"
        )
        .addResourceLocations(uploadPath);
    }
}