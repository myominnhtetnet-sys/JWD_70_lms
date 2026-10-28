package com.Learning_Managnment_System.JWD_70_lms.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 🟢 Absolute Path ဖြင့် src/main/resources/static/uploads/ နှင့် uploads/ နှစ်ခုစလုံးကို အမှန်တကယ် ချိတ်ဆက်ပေးခြင်း
        String staticUploadPath = new File("src/main/resources/static/uploads/").getAbsolutePath();
        String rootUploadPath = new File("uploads").getAbsolutePath();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + staticUploadPath + "/", "file:" + rootUploadPath + "/");

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
        
        // 🟢 FIXED: Removed the illegal '**/videos/**' wildcard. 
        // Explicitly maps root video folders and multi-tier nested student routes to satisfy the PathPatternParser rules perfectly!
        registry.addResourceHandler("/videos/**", "/student/videos/**", "/student/exam-take/videos/**", "/student/awards/videos/**")
                .addResourceLocations("classpath:/static/videos/");
        
        
    }
    
}
