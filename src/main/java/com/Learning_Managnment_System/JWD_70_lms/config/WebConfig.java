package com.Learning_Managnment_System.JWD_70_lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        // 🟢 FIXED: Change from "/uploads/" to "/uploads/**" so subfolders (like courses/) can be accessed correctly
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");

        registry.addResourceHandler("/css/", "/student/css/", "/student/exam-take/css/", "/student/awards/css/")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/", "/student/js/", "/student/exam-take/js/", "/student/awards/js/")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/images/", "/student/images/", "/student/exam-take/images/", "/student/awards/images/")
                .addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/assets/", "/student/assets/", "/student/exam-take/assets/", "/student/awards/assets/")
                .addResourceLocations("classpath:/static/assets/");
        registry.addResourceHandler("/webfonts/", "/student/webfonts/", "/student/exam-take/webfonts/", "/student/awards/webfonts/**")
                .addResourceLocations("classpath:/static/webfonts/");
	}
}