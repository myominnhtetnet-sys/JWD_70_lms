package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassSessionBean {
    private int sessionId;
    private String batchTitle;
    private String topic;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String meetingUrl;
}
