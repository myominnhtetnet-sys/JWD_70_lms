package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnnouncementBean {
	
	private Long announcementId;
    private Long batchId;
    private String batchTitle;    // JOIN ထားတဲ့ Batch Name ပြရန်
    private String title;
    private String body;
    private Long postedBy;
    private String postedByName;  // JOIN ထားတဲ့ User Name ပြရန်
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
