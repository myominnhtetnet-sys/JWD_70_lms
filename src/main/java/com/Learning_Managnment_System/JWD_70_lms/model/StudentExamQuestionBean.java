package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class StudentExamQuestionBean {
	private int questionId;
	private String questionText;
	private String questionType; // 'MCQ' or 'SHORT_ANSWER'
	private double questionMark;
	private List<QuestionOptionBean> options;
}
