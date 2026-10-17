package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.AwardRowMappers;
import com.Learning_Managnment_System.JWD_70_lms.model.StudentGradeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AwardRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Natively returns populated StudentGradeBeans using the static dashboard row mapper configuration
     */
    public List<StudentGradeBean> findAwardsAndGradesByEmail(String email) {
        String sql = "SELECT g.grade_id, g.enrollment_id, c.title AS course_title, " +
                     "g.assignment_score, g.exam_score, g.attendance_percent, g.total_score, " +
                     "g.grade_letter, g.result, cert.certificate_id, cert.certificate_code, " +
                     "cert.issued_at, cert.status AS cert_status " +
                     "FROM learning_db.grades g " +
                     "JOIN learning_db.enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "JOIN learning_db.batches b ON e.batch_id = b.batch_id " +
                     "JOIN learning_db.courses c ON b.course_id = c.course_id " +
                     "JOIN learning_db.users u ON e.user_id = u.user_id " +
                     "LEFT JOIN learning_db.certificates cert ON cert.enrollment_id = e.enrollment_id " +
                     "WHERE u.email = ? AND g.published_at IS NOT NULL " +
                     "ORDER BY g.published_at DESC";
                     
        return jdbcTemplate.query(sql, AwardRowMappers.DASHBOARD_MAPPER, email);
    }

    /**
     * Natively returns populated diploma Beans using the static diploma row mapper configuration
     */
    public List<StudentGradeBean> findCertificateDetailsByIdAndEmail(int certificateId, String email) {
        String sql = "SELECT cert.certificate_code, c.title AS course_title, u.full_name, cert.issued_at " +
                     "FROM learning_db.certificates cert " +
                     "JOIN learning_db.enrollments e ON cert.enrollment_id = e.enrollment_id " +
                     "JOIN learning_db.batches b ON e.batch_id = b.batch_id " +
                     "JOIN learning_db.courses c ON b.course_id = c.course_id " +
                     "JOIN learning_db.users u ON e.user_id = u.user_id " +
                     "WHERE cert.certificate_id = ? AND u.email = ? AND cert.status = 'ISSUED' LIMIT 1";
                     
        return jdbcTemplate.query(sql, AwardRowMappers.DIPLOMA_MAPPER, certificateId, email);
    }
}
