package com.Learning_Managnment_System.JWD_70_lms.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 🟢 Uploads ဖိုင်များအတွက် Absolute Path ချိတ်ဆက်ခြင်း (အလွန်မှန်ကန်သည်)
        String staticUploadPath = new File("src/main/resources/static/uploads/").getAbsolutePath();
        String rootUploadPath = new File("uploads").getAbsolutePath();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + staticUploadPath + "/", "file:" + rootUploadPath + "/");

        // 🟢 Sub-folders များပါ အလုပ်လုပ်စေရန် /** ထည့်သွင်းပေးခြင်း
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