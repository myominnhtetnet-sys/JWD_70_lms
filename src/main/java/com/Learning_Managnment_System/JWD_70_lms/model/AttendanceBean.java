package com.Learning_Managnment_System.JWD_70_lms.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class AttendanceBean {
	private Long sessionId;
    private List<AttendanceItem> attendanceList;

    public static class AttendanceItem {
        private Long enrollmentId;
        private String status;

        public Long getEnrollmentId() { return enrollmentId; }
        public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public List<AttendanceItem> getAttendanceList() { return attendanceList; }
    public void setAttendanceList(List<AttendanceItem> attendanceList) { this.attendanceList = attendanceList; }
}