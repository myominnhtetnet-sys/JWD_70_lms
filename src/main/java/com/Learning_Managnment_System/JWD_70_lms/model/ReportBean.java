
package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ReportBean {

    private Long reportId;
    private String reportTitle;
    private String category;
    private String format;
    private String fileName;
    private String originalFileName;
    private long fileSize;
    private LocalDateTime createdAt;

}
