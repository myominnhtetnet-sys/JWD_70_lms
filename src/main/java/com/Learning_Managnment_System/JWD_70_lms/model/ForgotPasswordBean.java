package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordBean {
	private String email;
	private String code;
	private String newPassword;
	private String confirmPassword;
}