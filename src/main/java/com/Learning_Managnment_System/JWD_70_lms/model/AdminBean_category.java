package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminBean_category {
	
	private int id;
	private Integer category_id;
	private Integer parent_id;
	private String name;
	private String slug;
	private String description;
	private String is_active;
	private String created_at;
	private String updated_at;

}
