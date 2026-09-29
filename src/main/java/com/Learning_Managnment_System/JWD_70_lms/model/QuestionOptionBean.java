package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class QuestionOptionBean {

	private Long optionId;
	private Long questionId;
	private String optionLabel;
	private String optionText;
	private Boolean isCorrect;
}
