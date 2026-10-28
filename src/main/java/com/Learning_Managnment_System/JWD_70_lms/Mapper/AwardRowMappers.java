package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentGradeBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AwardRowMappers {

    /**
     * 🟢 Reusable RowMapper for the main awards dashboard list query
     */
    public static final RowMapper<StudentGradeBean> DASHBOARD_MAPPER = (rs, rowNum) -> {
        StudentGradeBean grade = new StudentGradeBean();
        grade.setGradeId(rs.getInt("grade_id"));
        grade.setEnrollmentId(rs.getInt("enrollment_id"));
        grade.setCourseTitle(rs.getString("course_title"));
        grade.setAssignmentScore(((Number) rs.getObject("assignment_score")).doubleValue());
        grade.setExamScore(((Number) rs.getObject("exam_score")).doubleValue());
        grade.setAttendancePercent(((Number) rs.getObject("attendance_percent")).doubleValue());
        grade.setTotalScore(((Number) rs.getObject("total_score")).doubleValue());
        grade.setGradeLetter(rs.getString("grade_letter"));
        grade.setResult(rs.getString("result"));
        
        int certId = rs.getInt("certificate_id");
        if (!rs.wasNull()) {
            CertificateBean certificate = new CertificateBean();
            certificate.setCertificateId(certId);
            certificate.setCertificateCode(rs.getString("certificate_code"));
            certificate.setIssuedAt(rs.getTimestamp("issued_at").toString()); // Kept raw for Service to clean
            certificate.setCertStatus(rs.getString("cert_status"));
            
            grade.setCertificate(certificate);
        }
        return grade;
    };

    /**
     * 🟢 Reusable RowMapper for the printable single diploma certificate query
     */
    public static final RowMapper<StudentGradeBean> DIPLOMA_MAPPER = (rs, rowNum) -> {
        StudentGradeBean grade = new StudentGradeBean();
        grade.setCourseTitle(rs.getString("course_title"));
        
        CertificateBean cert = new CertificateBean();
        cert.setCertificateCode(rs.getString("certificate_code"));
        cert.setStudentName(rs.getString("full_name"));
        cert.setIssuedAt(rs.getTimestamp("issued_at").toString()); // Kept raw for Service to clean
        
        grade.setCertificate(cert);
        return grade;
    };
}
