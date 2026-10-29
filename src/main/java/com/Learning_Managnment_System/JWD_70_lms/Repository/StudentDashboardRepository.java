package com.Learning_Managnment_System.JWD_70_lms.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class StudentDashboardRepository {

	@Autowired
  JdbcTemplate jdbcTemplate;

  

    public Integer findUserIdByEmail(String email) {
        try {
            String userSql = "SELECT user_id FROM users WHERE email = ? LIMIT 1";
            return jdbcTemplate.queryForObject(userSql, Integer.class, email);
        } catch (Exception e) {
            return null;
        }
    }

    public int getEnrolledCount(int userId) {
        try {
            String countSql = "SELECT COUNT(*) FROM enrollments WHERE user_id = ?";
            Integer count = jdbcTemplate.queryForObject(countSql, Integer.class, userId);
            return count != null ? count : 0;
        } catch (Exception e) {
            try {
                String altCountSql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ?";
                Integer count = jdbcTemplate.queryForObject(altCountSql, Integer.class, userId);
                return count != null ? count : 0;
            } catch (Exception ex) { 
                return 0; 
            }
        }
    }

    public int getPendingTasksCount(int userId) {
        try {
            String pendingSql = """
                SELECT COUNT(*) 
                FROM assignments a
                JOIN enrollments e ON a.batch_id = e.batch_id
                LEFT JOIN submissions s ON a.assignment_id = s.assignment_id 
                                       AND e.enrollment_id = s.enrollment_id
                WHERE e.user_id = ? 
                  AND s.submission_id IS NULL
                """;
            Integer count = jdbcTemplate.queryForObject(pendingSql, Integer.class, userId);
            return count != null ? count : 0;
        } catch (Exception e) {
            try {
                String altPendingSql = """
                    SELECT COUNT(*) 
                    FROM assignments a
                    JOIN enrollments e ON a.batch_id = e.batch_id
                    LEFT JOIN submissions s ON a.assignment_id = s.assignment_id 
                                           AND e.enrollment_id = s.enrollment_id
                    WHERE e.student_id = ? 
                      AND s.submission_id IS NULL
                    """;
                Integer count = jdbcTemplate.queryForObject(altPendingSql, Integer.class, userId);
                return count != null ? count : 0;
            } catch (Exception ex) {
                System.err.println("DB NOTICE -> Defaulting pending count to 0. Error: " + ex.getMessage());
                return 0; 
            }
        }
    }

    public int getPassedExamsCount(int userId) {
        try {
            String passedSql = """
                SELECT COUNT(*) 
                FROM exam_attempts ex
                JOIN enrollments e ON ex.enrollment_id = e.enrollment_id
                WHERE e.user_id = ? AND ex.result = 'PASSED'
                """;
            Integer count = jdbcTemplate.queryForObject(passedSql, Integer.class, userId);
            return count != null ? count : 0;
        } catch (Exception e) {
            try {
                String altPassedSql = """
                    SELECT COUNT(*) 
                    FROM student_exams ex
                    JOIN enrollments e ON ex.enrollment_id = e.enrollment_id
                    WHERE e.user_id = ? AND ex.result = 'PASSED'
                    """;
                Integer count = jdbcTemplate.queryForObject(altPassedSql, Integer.class, userId);
                return count != null ? count : 0;
            } catch (Exception ex1) {
                try {
                    String studentIdPassedSql = """
                        SELECT COUNT(*) 
                        FROM exam_attempts ex
                        JOIN enrollments e ON ex.enrollment_id = e.enrollment_id
                        WHERE e.student_id = ? AND ex.result = 'PASSED'
                        """;
                    Integer count = jdbcTemplate.queryForObject(studentIdPassedSql, Integer.class, userId);
                    return count != null ? count : 0;
                } catch (Exception ex2) {
                    System.err.println("DB NOTICE -> Handled passed exams fallback logic exception safely. Defaulting to 0.");
                    return 0;
                }
            }
        }
    }

    public int getCertificatesCount(int userId) {
        try {
            String certsSql = "SELECT COUNT(*) FROM certificates c JOIN enrollments e ON c.enrollment_id = e.enrollment_id WHERE e.user_id = ? AND c.status = 'ISSUED'";
            Integer count = jdbcTemplate.queryForObject(certsSql, Integer.class, userId);
            return count != null ? count : 0;
        } catch (Exception e) {
            try {
                String altCertsSql = "SELECT COUNT(*) FROM certificates WHERE user_id = ?";
                Integer count = jdbcTemplate.queryForObject(altCertsSql, Integer.class, userId);
                return count != null ? count : 0;
            } catch (Exception ex) { 
                return 0; 
            }
        }
    }

    public List<Map<String, Object>> getActiveCourses(int userId) {
        try {
            String coursesSql = """
                SELECT 
                    b.batch_code AS batchName, 
                    b.title AS title, 
                    0 AS progress 
                FROM enrollments e 
                JOIN batches b ON e.batch_id = b.batch_id 
                WHERE e.user_id = ?
                """;
            return jdbcTemplate.queryForList(coursesSql, userId);
        } catch (Exception e) {
            try {
                String altCoursesSql = """
                    SELECT 
                        b.batch_name AS batchName, 
                        b.title AS title, 
                        0 AS progress 
                    FROM enrollments e 
                    JOIN batches b ON e.batch_id = b.batch_id 
                    WHERE e.student_id = ?
                    """;
                return jdbcTemplate.queryForList(altCoursesSql, userId);
            } catch (Exception ex) {
                return new ArrayList<>();
            }
        }
    }
}
