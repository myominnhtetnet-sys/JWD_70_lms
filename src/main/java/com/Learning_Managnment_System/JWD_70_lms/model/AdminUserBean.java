package com.Learning_Managnment_System.JWD_70_lms.model;


import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserBean {

	private Integer user_id;
	    private int role_id;
	    private String role_name;
	    private String full_name;
	    private String email;
	    private String phone;
	    private String password_hash;
	    private LocalDate dob;
	    private String gender;
	    private String address;
	    private String status;
	

private String password;

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}

	    
}