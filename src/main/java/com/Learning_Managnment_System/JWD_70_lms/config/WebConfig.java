package com.Learning_Managnment_System.JWD_70_lms.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Uploaded files: assignment attachments and submissions
        Path uploadDirectory = Paths.get("uploads")
                .toAbsolutePath()
                .normalize();

        String uploadLocation = uploadDirectory.toUri().toString();

        if (!uploadLocation.endsWith("/")) {
            uploadLocation += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);

        // CSS
        registry.addResourceHandler(
                "/css/**",
                "/student/css/**",
                "/student/exam-take/css/**",
                "/student/awards/css/**"
        ).addResourceLocations("classpath:/static/css/");

        // JavaScript
        registry.addResourceHandler(
                "/js/**",
                "/student/js/**",
                "/student/exam-take/js/**",
                "/student/awards/js/**"
        ).addResourceLocations("classpath:/static/js/");

        // Images
        registry.addResourceHandler(
                "/images/**",
                "/student/images/**",
                "/student/exam-take/images/**",
                "/student/awards/images/**"
        ).addResourceLocations("classpath:/static/images/");

        // Assets
        registry.addResourceHandler(
                "/assets/**",
                "/student/assets/**",
                "/student/exam-take/assets/**",
                "/student/awards/assets/**"
        ).addResourceLocations("classpath:/static/assets/");

        // Web fonts
        registry.addResourceHandler(
                "/webfonts/**",
                "/student/webfonts/**",
                "/student/exam-take/webfonts/**",
                "/student/awards/webfonts/**"
        ).addResourceLocations("classpath:/static/webfonts/");

        System.out.println("Upload folder: " + uploadDirectory);
    }
}