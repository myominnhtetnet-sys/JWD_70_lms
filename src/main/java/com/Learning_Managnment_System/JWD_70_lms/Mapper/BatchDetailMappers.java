package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.BatchDetailBean.*;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchDetailMappers {
	
	// 1. Student Row Mapper
    public static class BatchStudentRowMapper implements RowMapper<StudentInfo> {
        @Override
        public StudentInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudentInfo s = new StudentInfo();
            s.setEnrollmentId(rs.getLong("enrollment_id"));
            s.setUserId(rs.getLong("user_id"));
            s.setFullName(rs.getString("full_name"));
            s.setEmail(rs.getString("email"));
            s.setPaymentStatus(rs.getString("payment_status"));
            s.setEnrollmentStatus(rs.getString("status"));
            
            if (rs.getTimestamp("enrolled_at") != null) {
                s.setEnrolledAt(rs.getTimestamp("enrolled_at").toLocalDateTime());
            }
            return s;
        }
    }

    // 2. Class Session Row Mapper (Handling Null Foreign Keys Properly)
    public static class ClassSessionRowMapper implements RowMapper<ClassSessionInfo> {
        @Override
        public ClassSessionInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            ClassSessionInfo cs = new ClassSessionInfo();
            cs.setSessionId(rs.getLong("session_id"));
            cs.setBatchId(rs.getLong("batch_id"));
            
            long lessonId = rs.getLong("lesson_id");
            cs.setLessonId(rs.wasNull() ? null : lessonId);
            
            cs.setTitle(rs.getString("title"));
            
            if (rs.getTimestamp("start_at") != null) {
                cs.setStartAt(rs.getTimestamp("start_at").toLocalDateTime());
            }
            if (rs.getTimestamp("end_at") != null) {
                cs.setEndAt(rs.getTimestamp("end_at").toLocalDateTime());
            }
            
            cs.setMeetingUrl(rs.getString("meeting_url"));
            cs.setStatus(rs.getString("status"));
            return cs;
        }
    }

    // 3. Student Progress Row Mapper
    public static class StudentProgressRowMapper implements RowMapper<StudentProgress> {
        @Override
        public StudentProgress mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudentProgress sp = new StudentProgress();
            sp.setEnrollmentId(rs.getLong("enrollment_id"));
            sp.setStudentName(rs.getString("full_name"));
            sp.setCompletedLessons(rs.getInt("completed_lessons"));
            sp.setTotalLessons(rs.getInt("total_lessons"));
            sp.setOverallProgressPercent(rs.getDouble("avg_progress"));
            return sp;
        }
    }

    // 4. Batch Announcement Row Mapper
    public static class BatchAnnouncementRowMapper implements RowMapper<AnnouncementInfo> {
        @Override
        public AnnouncementInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            AnnouncementInfo a = new AnnouncementInfo();
            a.setAnnouncementId(rs.getLong("announcement_id"));
            a.setBatchId(rs.getLong("batch_id"));
            a.setTitle(rs.getString("title"));
            a.setBody(rs.getString("body"));
            a.setPostedByName(rs.getString("posted_by_name"));
            
            if (rs.getTimestamp("published_at") != null) {
                a.setPublishedAt(rs.getTimestamp("published_at").toLocalDateTime());
            }
            return a;
        }
    }

}
