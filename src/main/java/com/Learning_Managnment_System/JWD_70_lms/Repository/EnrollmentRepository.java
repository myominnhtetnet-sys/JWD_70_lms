package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.Enrollment;

@Repository
public class EnrollmentRepository {
	

    @Autowired
    private JdbcTemplate jdbcTemplate;



    public List<Enrollment> findAll() {

        String sql = """
            SELECT
                e.enrollment_id,
                e.user_id,
                u.full_name AS student_name,
                u.email,

                e.batch_id,
                b.batch_code,
                b.title AS batch_title,

                c.title AS course_title,

                e.enrolled_at,
                e.original_amount,
                e.discount_amount,
                e.final_amount,

                e.payment_type,
                e.payment_status,
                e.status

            FROM enrollments e

            INNER JOIN users u
                ON e.user_id = u.user_id

            INNER JOIN batches b
                ON e.batch_id = b.batch_id

            INNER JOIN courses c
                ON b.course_id = c.course_id

            WHERE u.deleted_at IS NULL

            ORDER BY e.enrolled_at DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            Enrollment enrollment = new Enrollment();

            enrollment.setEnrollmentId(
                rs.getInt("enrollment_id")
            );

            enrollment.setUserId(
                rs.getInt("user_id")
            );

            enrollment.setStudentName(
                rs.getString("student_name")
            );

            enrollment.setEmail(
                rs.getString("email")
            );

            enrollment.setBatchId(
                rs.getInt("batch_id")
            );

            enrollment.setBatchCode(
                rs.getString("batch_code")
            );

            enrollment.setBatchTitle(
                rs.getString("batch_title")
            );

            enrollment.setCourseTitle(
                rs.getString("course_title")
            );

            enrollment.setEnrolledAt(
                rs.getString("enrolled_at")
            );

            enrollment.setOriginalAmount(
                rs.getDouble("original_amount")
            );

            enrollment.setDiscountAmount(
                rs.getDouble("discount_amount")
            );

            enrollment.setFinalAmount(
                rs.getDouble("final_amount")
            );

            enrollment.setPaymentType(
                rs.getString("payment_type")
            );

            enrollment.setPaymentStatus(
                rs.getString("payment_status")
            );

            enrollment.setStatus(
                rs.getString("status")
            );

            return enrollment;
        });
    }
	

}
