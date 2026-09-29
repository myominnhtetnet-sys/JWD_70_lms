package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionBean {

	private Long questionId;
	private Long courseId;
	private String questionText;
	private String questionType;
	private BigDecimal defaultMark;
	private String explanation;
	private Boolean isActive;
	private Long createdBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
