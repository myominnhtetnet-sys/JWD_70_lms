package com.Learning_Managnment_System.JWD_70_lms.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuditLogBean {
	
	private Long logId;
    private Long userId;
    private String userName;     // users table မှ full_name ကို JOIN ယူရန်
    private String actionType;   // 'CREATE', 'UPDATE', 'DELETE'
    private String tableName;    // 'users', 'courses', etc.
    private String recordId;
    private String oldValues;    // JSON String
    private String newValues;    // JSON String
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;

}
