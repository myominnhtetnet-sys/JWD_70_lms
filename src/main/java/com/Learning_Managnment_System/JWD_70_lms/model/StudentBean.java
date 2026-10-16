package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter

public class StudentBean {
	private Integer userId;
    private Integer roleId = 3;
    private String fullName;        
    private String email;
    private String phone;
    private String passwordHash;   
    private LocalDate dob;
    private String gender;
    private String address;
    private String profileImage;  
    private String status = "ACTIVE";
    private LocalDateTime emailVerifiedAt; 
    private LocalDateTime lastLoginAt;    
    private LocalDateTime createdAt;       
    private LocalDateTime updatedAt;      
    private LocalDateTime deletedAt;
	
}
