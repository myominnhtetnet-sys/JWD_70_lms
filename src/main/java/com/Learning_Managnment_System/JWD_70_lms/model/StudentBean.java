package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class StudentBean {
	private int id;
	private int role_id;
	private String name;
	private String email;
	private String password;
	private int age;
	private String address;
	private LocalDate dob;
	private String gender;
	
}
