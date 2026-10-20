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
        // Robust external file path configuration for uploads
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toUri().toString();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);

        // FIXED: Added '**' to all handlers so nested files and subpaths resolve correctly
        registry.addResourceHandler("/css/**", "/student/css/**", "/student/exam-take/css/**", "/student/awards/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**", "/student/js/**", "/student/exam-take/js/**", "/student/awards/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/images/**", "/student/images/**", "/student/exam-take/images/**", "/student/awards/images/**")
                .addResourceLocations("classpath:/static/images/");

        registry.addResourceHandler("/assets/**", "/student/assets/**", "/student/exam-take/assets/**", "/student/awards/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        registry.addResourceHandler("/webfonts/**", "/student/webfonts/**", "/student/exam-take/webfonts/**", "/student/awards/webfonts/**")
                .addResourceLocations("classpath:/static/webfonts/");
    }
}
