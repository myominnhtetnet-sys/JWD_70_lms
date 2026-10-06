package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserBean {

	 private int user_id;
	    private int role_id;

	    private String full_name;
	    private String email;
	    private String phone;
	    private String password_hash;

	    private String dob;
	    private String gender;
	    private String address;

	    private String status;

	    private String role_name;

	    private LocalDateTime created_at;
	    private LocalDateTime updated_at;
}