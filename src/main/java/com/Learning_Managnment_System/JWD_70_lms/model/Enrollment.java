package com.Learning_Managnment_System.JWD_70_lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Enrollment {
	
	 private int enrollmentId;
	    private int userId;
	    private String studentName;
	    private String email;

	    private int batchId;
	    private String batchCode;
	    private String batchTitle;

	    private String courseTitle;

	    private String enrolledAt;

	    private double originalAmount;
	    private double discountAmount;
	    private double finalAmount;

	    private String paymentType;
	    private String paymentStatus;
	    private String status;

}
