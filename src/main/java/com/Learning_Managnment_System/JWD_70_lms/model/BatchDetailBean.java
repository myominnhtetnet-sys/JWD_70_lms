package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BatchDetailBean {
	
	// *** အသစ်ထည့်လိုက်သည့် Batch Dropdown အတွက် Model ***
	@Getter @Setter
	public static class BatchInfo {
		private Long batchId;
		private String batchCode;
		private String title;
	}
	
	@Getter @Setter
	public static class LessonInfo {
	    private Long lessonId;
	    private String title;
	}

	@Getter @Setter
	public static class StudentInfo{
		private Long enrollmentId;
		private Long userId;
		private String fullName;
		private String email;
		private String paymentStatus;
		private String enrollmentStatus;
		private LocalDateTime enrolledAt;
	}
	
	@Getter @Setter
	public static class ClassSessionInfo{
		private Long sessionId;
		private Long batchId;
		private Long lessonId;
		private String title;
		
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
		private LocalDateTime startAt;
		
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
		private LocalDateTime endAt;
		
		private String meetingUrl;
		private String status;
	}	

	@Getter @Setter
	public static class StudentProgress{
		private Long enrollmentId;
		private String studentName;
		private Integer completedLessons;
		private Integer totalLessons;
		private Double overallProgressPercent;
	}
	
	@Getter @Setter
	public static class AnnouncementInfo{
		private Long announcementId;
		private Long batchId;
		private String title;
		private String body;
		private String postedByName;
		private LocalDateTime publishedAt;
	}
}