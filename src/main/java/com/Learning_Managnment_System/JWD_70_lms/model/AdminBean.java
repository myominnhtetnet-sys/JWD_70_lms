package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminBean {
	
	private int id;
	private int category_id;
	private int parent_id;
	private String name;
	private String slug;
	private String description;
	private String is_active;
	private String created_at;
	private String updated_at;

}
