package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.CertificateBean;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentGradeBean;

public class AwardRowMappers {

    /**
     * RowMapper for the main awards dashboard list query.
     */
    public static final RowMapper<StudentGradeBean> DASHBOARD_MAPPER =
            (rs, rowNum) -> {

        StudentGradeBean grade = new StudentGradeBean();

        grade.setGradeId(rs.getInt("grade_id"));
        grade.setEnrollmentId(rs.getInt("enrollment_id"));
        grade.setCourseTitle(rs.getString("course_title"));

        grade.setAssignmentScore(getDouble(rs, "assignment_score"));
        grade.setExamScore(getDouble(rs, "exam_score"));
        grade.setAttendancePercent(getDouble(rs, "attendance_percent"));
        grade.setTotalScore(getDouble(rs, "total_score"));

        grade.setGradeLetter(rs.getString("grade_letter"));
        grade.setResult(rs.getString("result"));

        int certId = rs.getInt("certificate_id");

        if (!rs.wasNull()) {

            CertificateBean certificate = new CertificateBean();

            certificate.setCertificateId(certId);
            certificate.setCertificateCode(
                    rs.getString("certificate_code"));

            certificate.setIssuedAt(
                    getLocalDateTime(rs, "issued_at"));

            certificate.setStatus(
                    rs.getString("cert_status"));

            grade.setCertificate(certificate);
        }

        return grade;
    };

    /**
     * RowMapper for the printable diploma certificate query.
     */
    public static final RowMapper<StudentGradeBean> DIPLOMA_MAPPER =
            (rs, rowNum) -> {

        StudentGradeBean grade = new StudentGradeBean();

        grade.setCourseTitle(rs.getString("course_title"));

        CertificateBean certificate = new CertificateBean();

        certificate.setCertificateCode(
                rs.getString("certificate_code"));

        certificate.setStudentName(
                rs.getString("full_name"));

        certificate.setIssuedAt(
                getLocalDateTime(rs, "issued_at"));

        grade.setCertificate(certificate);

        return grade;
    };

    /**
     * Convert nullable SQL numeric values to double.
     */
    private static double getDouble(ResultSet rs, String column)
            throws SQLException {

        Number value = (Number) rs.getObject(column);

        return value != null ? value.doubleValue() : 0.0;
    }

    /**
     * Convert nullable SQL Timestamp to LocalDateTime.
     */
    private static java.time.LocalDateTime getLocalDateTime(
            ResultSet rs, String column) throws SQLException {

        Timestamp timestamp = rs.getTimestamp(column);

        return timestamp != null
                ? timestamp.toLocalDateTime()
                : null;
    }
}