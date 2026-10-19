
package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FinalGradeRepository {

    private final JdbcTemplate jdbcTemplate;

    public FinalGradeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getBatches() {
        String sql = """
            SELECT b.batch_id, b.batch_code, b.title, c.title AS course_title
            FROM batches b
            JOIN courses c ON c.course_id = b.course_id
            WHERE b.status <> 'CANCELLED'
            ORDER BY b.start_date DESC
            """;

        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getStudentsForGrades(int batchId) {

        String sql = """
            SELECT
                e.enrollment_id,
                u.user_id,
                u.full_name AS student_name,
                u.email,

                COALESCE((
                    SELECT AVG(
                        CASE
                            WHEN a.total_mark > 0
                            THEN LEAST(100.0,
                                GREATEST(0.0,
                                    s.mark / a.total_mark * 100.0
                                )
                            )
                            ELSE NULL
                        END
                    )
                    FROM assignments a
                    JOIN submissions s
                      ON s.assignment_id = a.assignment_id
                     AND s.enrollment_id = e.enrollment_id
                     AND s.status = 'GRADED'
                     AND s.mark IS NOT NULL
                    WHERE a.batch_id = e.batch_id
                      AND a.status IN ('PUBLISHED', 'CLOSED')
                ), 0) AS assignment_percent,

                COALESCE((
                    SELECT AVG(best_exam.exam_percent)
                    FROM (
                        SELECT
                            ea.exam_id,
                            MAX(
                                CASE
                                    WHEN ex.total_mark > 0
                                    THEN LEAST(100.0,
                                        GREATEST(0.0,
                                            ea.score / ex.total_mark * 100.0
                                        )
                                    )
                                    ELSE NULL
                                END
                            ) AS exam_percent
                        FROM exam_attempts ea
                        JOIN exams ex ON ex.exam_id = ea.exam_id
                        WHERE ea.enrollment_id = e.enrollment_id
                          AND ea.status = 'GRADED'
                          AND ea.score IS NOT NULL
                          AND (ex.batch_id = e.batch_id
                               OR (ex.batch_id IS NULL
                                   AND ex.course_id = b.course_id))
                          AND ex.status IN ('PUBLISHED', 'CLOSED')
                        GROUP BY ea.exam_id
                    ) best_exam
                ), 0) AS exam_percent,

                COALESCE((
                    SELECT
                        100.0 *
                        SUM(CASE
                            WHEN att.status IN ('PRESENT', 'LATE')
                            THEN 1 ELSE 0
                        END)
                        / NULLIF(
                            SUM(CASE
                                WHEN att.status = 'EXCUSED' THEN 0
                                ELSE 1
                            END), 0
                        )
                    FROM class_sessions cs
                    LEFT JOIN attendance att
                      ON att.session_id = cs.session_id
                     AND att.enrollment_id = e.enrollment_id
                    WHERE cs.batch_id = e.batch_id
                      AND cs.status = 'DONE'
                ), 0) AS attendance_percent,

                g.published_at

            FROM enrollments e
            JOIN users u ON u.user_id = e.user_id
            JOIN batches b ON b.batch_id = e.batch_id
            LEFT JOIN grades g ON g.enrollment_id = e.enrollment_id

            WHERE e.batch_id = ?
              AND e.status IN ('ACTIVE', 'COMPLETED')
              AND u.deleted_at IS NULL

            ORDER BY u.full_name
            """;

        return jdbcTemplate.queryForList(sql, batchId);
    }

    public void publishGrade(
            int enrollmentId,
            java.math.BigDecimal assignmentScore,
            java.math.BigDecimal examScore,
            java.math.BigDecimal attendancePercent,
            java.math.BigDecimal totalScore,
            String gradeLetter,
            String result,
            int teacherId) {

        String sql = """
            INSERT INTO grades (
                enrollment_id,
                assignment_score,
                exam_score,
                attendance_percent,
                total_score,
                grade_letter,
                result,
                published_at,
                graded_by
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?)

            ON DUPLICATE KEY UPDATE
                assignment_score = VALUES(assignment_score),
                exam_score = VALUES(exam_score),
                attendance_percent = VALUES(attendance_percent),
                total_score = VALUES(total_score),
                grade_letter = VALUES(grade_letter),
                result = VALUES(result),
                published_at = NOW(),
                graded_by = VALUES(graded_by)
            """;

        jdbcTemplate.update(
            sql,
            enrollmentId,
            assignmentScore,
            examScore,
            attendancePercent,
            totalScore,
            gradeLetter,
            result,
            teacherId
        );
    }
    

public int countUngradedAssessments(int batchId) {

    String sql = """
        SELECT COUNT(*)
        FROM enrollments e
        JOIN assignments a
          ON a.batch_id = e.batch_id
         AND a.status IN ('PUBLISHED', 'CLOSED')
        LEFT JOIN submissions s
          ON s.assignment_id = a.assignment_id
         AND s.enrollment_id = e.enrollment_id
         AND s.status = 'GRADED'
         AND s.mark IS NOT NULL
        WHERE e.batch_id = ?
          AND e.status IN ('ACTIVE', 'COMPLETED')
          AND s.submission_id IS NULL

        UNION ALL

        SELECT COUNT(*)
        FROM enrollments e
        JOIN batches b ON b.batch_id = e.batch_id
        JOIN exams ex
          ON (ex.batch_id = e.batch_id
              OR (ex.batch_id IS NULL
                  AND ex.course_id = b.course_id))
         AND ex.status IN ('PUBLISHED', 'CLOSED')
        LEFT JOIN exam_attempts ea
          ON ea.exam_id = ex.exam_id
         AND ea.enrollment_id = e.enrollment_id
         AND ea.status = 'GRADED'
         AND ea.score IS NOT NULL
        WHERE e.batch_id = ?
          AND e.status IN ('ACTIVE', 'COMPLETED')
          AND ea.attempt_id IS NULL
        """;

    List<Map<String, Object>> rows =
            jdbcTemplate.queryForList(sql, batchId, batchId);

    return rows.stream()
            .mapToInt(row ->
                ((Number) row.get("COUNT(*)")).intValue())
            .sum();
}

}
