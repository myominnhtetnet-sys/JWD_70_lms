package com.Learning_Managnment_System.JWD_70_lms.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve the absolute path location of your physical upload folder
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        // Maps public uploads safely to local hard disk directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
        
        registry.addResourceHandler("/certs/**")
        .addResourceLocations("file:certs/");
        
        // Explicitly handle root paths AND nested paths separately for CSS assets
        registry.addResourceHandler("/css/**", "/student/css/**", "/student/exam-take/css/**", "/student/awards/css/**")
                .addResourceLocations("classpath:/static/css/");
        
        // Explicitly handle JavaScript routes
        registry.addResourceHandler("/js/**", "/student/js/**", "/student/exam-take/js/**", "/student/awards/js/**")
                .addResourceLocations("classpath:/static/js/");
		        
        // Explicitly handle image routes
        registry.addResourceHandler("/images/**", "/student/images/**", "/student/exam-take/images/**", "/student/awards/images/**")
                .addResourceLocations("classpath:/static/images/");
		        
        // Explicitly handle assets packages
        registry.addResourceHandler("/assets/**", "/student/assets/**", "/student/exam-take/assets/**", "/student/awards/assets/**")
                .addResourceLocations("classpath:/static/assets/");
		        
        // Explicitly handle webfonts packages
        registry.addResourceHandler("/webfonts/**", "/student/webfonts/**", "/student/exam-take/webfonts/**", "/student/awards/webfonts/**")
                .addResourceLocations("classpath:/static/webfonts/");
        
        // 🟢 FIXED: Removed the illegal '**/videos/**' wildcard. 
        // Explicitly maps root video folders and multi-tier nested student routes to satisfy the PathPatternParser rules perfectly!
        registry.addResourceHandler("/videos/**", "/student/videos/**", "/student/exam-take/videos/**", "/student/awards/videos/**")
                .addResourceLocations("classpath:/static/videos/");
        
        
    }
    
}
