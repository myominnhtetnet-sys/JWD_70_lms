package com.Learning_Managnment_System.JWD_70_lms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentBean {

	private Integer assignmentId;
	private Integer batchId;
	private Integer lessonId;
	private String title;
	private String description;
	private String attachment;
	private LocalDateTime startAt;
	private LocalDateTime dueAt;
	private Boolean allowLateSubmit;
	private LocalDateTime lateDeadline;
	private BigDecimal totalMark;
	private BigDecimal passMark;
	private String status;
	private Integer createdBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
