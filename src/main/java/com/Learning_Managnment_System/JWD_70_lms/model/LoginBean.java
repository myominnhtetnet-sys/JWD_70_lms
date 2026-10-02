package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginBean {
	private int role_id;
	private String full_name;
    private String email;
    private String password_hash;
    
    
}
