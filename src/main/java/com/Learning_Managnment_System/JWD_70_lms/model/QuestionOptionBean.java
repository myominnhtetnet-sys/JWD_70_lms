package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionOptionBean {

	private Integer optionId;
	private Integer questionId;
	private String optionLabel;
	private String optionText;
	private Boolean isCorrect;
}
