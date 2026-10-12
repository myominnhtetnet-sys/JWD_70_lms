package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "students") // Maps this class to your database table
public class StudentBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Or whatever your primary key field name is

    private String fullName;
	private Integer userId;
    private Integer roleId = 3;     
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
