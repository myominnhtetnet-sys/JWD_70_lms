package com.Learning_Managnment_System.JWD_70_lms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan("com.Learning_Managnment_System.JWD_70_lms.Mapper")

public class Jwd70LmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Jwd70LmsApplication.class, args);
	}

}


