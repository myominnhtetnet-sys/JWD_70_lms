package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FinalGradeRepository {

    private final JdbcTemplate jdbc;

    public FinalGradeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> findTeacherBatches(int teacherId) {
        return jdbc.queryForList("""
            SELECT b.batch_id AS batchId,
                   b.batch_code AS batchCode,
                   b.title
            FROM batches b
            JOIN batch_instructors bi ON bi.batch_id = b.batch_id
            WHERE bi.instructor_id = ?
              AND b.status <> 'CANCELLED'
            ORDER BY b.start_date DESC, b.batch_id DESC
            """, teacherId);
    }

    public Map<String, Object> requireBatch(
            int batchId, int teacherId, boolean lock) {

        String sql = """
            SELECT b.batch_id AS batchId,
                   b.course_id AS courseId,
                   b.batch_code AS batchCode,
                   b.title
            FROM batches b
            JOIN batch_instructors bi ON bi.batch_id = b.batch_id
            WHERE b.batch_id = ?
              AND bi.instructor_id = ?
              AND b.status <> 'CANCELLED'
            """ + (lock ? " FOR UPDATE" : "");

        List<Map<String, Object>> rows =
                jdbc.queryForList(sql, batchId, teacherId);

        if (rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "Batch not found or access denied.");
        }

        return rows.get(0);
    }

    public List<Map<String, Object>> findStudents(int batchId) {
        return jdbc.queryForList("""
            SELECT en.enrollment_id AS enrollmentId,
                   u.full_name AS studentName,
                   g.grade_id AS gradeId,
                   g.assignment_score AS assignmentScore,
                   g.exam_score AS examScore,
                   g.attendance_percent AS attendancePercent,
                   g.total_score AS totalScore,
                   g.grade_letter AS gradeLetter,
                   g.result,
                   g.published_at AS publishedAt
            FROM enrollments en
            JOIN users u ON u.user_id = en.user_id
            LEFT JOIN grades g ON g.enrollment_id = en.enrollment_id
            WHERE en.batch_id = ?
              AND en.status IN ('ACTIVE', 'COMPLETED')
            ORDER BY u.full_name, en.enrollment_id
            """, batchId);
    }

    public List<Map<String, Object>> findAssignments(int batchId) {
        return jdbc.queryForList("""
            SELECT assignment_id AS assignmentId,
                   title, status,
                   total_mark AS totalMark
            FROM assignments
            WHERE batch_id = ?
              AND status <> 'DRAFT'
            ORDER BY assignment_id
            """, batchId);
    }

    public List<Map<String, Object>> findExams(
            int batchId, int courseId) {

        return jdbc.queryForList("""
            SELECT exam_id AS examId,
                   title, status,
                   total_mark AS totalMark
            FROM exams
            WHERE course_id = ?
              AND (batch_id = ? OR batch_id IS NULL)
              AND status <> 'DRAFT'
            ORDER BY exam_id
            """, courseId, batchId);
    }

    public List<Map<String, Object>> findSubmission(
            int assignmentId, int enrollmentId) {

        return jdbc.queryForList("""
            SELECT status, mark
            FROM submissions
            WHERE assignment_id = ?
              AND enrollment_id = ?
            """, assignmentId, enrollmentId);
    }

    public List<Map<String, Object>> findAttempts(
            int examId, int enrollmentId) {

        return jdbc.queryForList("""
            SELECT status, score, result
            FROM exam_attempts
            WHERE exam_id = ?
              AND enrollment_id = ?
            ORDER BY attempt_no
            """, examId, enrollmentId);
    }

    public int countScheduledSessions(int batchId) {
        return jdbc.queryForObject("""
            SELECT COUNT(*)
            FROM class_sessions
            WHERE batch_id = ?
              AND status = 'SCHEDULED'
            """, Integer.class, batchId);
    }

    public List<Map<String, Object>> findAttendance(
            int batchId, int enrollmentId) {

        return jdbc.queryForList("""
            SELECT cs.session_id AS sessionId,
                   a.status
            FROM class_sessions cs
            JOIN enrollments en
              ON en.enrollment_id = ?
             AND en.batch_id = cs.batch_id
            LEFT JOIN attendance a
              ON a.session_id = cs.session_id
             AND a.enrollment_id = en.enrollment_id
            WHERE cs.batch_id = ?
              AND cs.status = 'DONE'
              AND cs.start_at >= en.enrolled_at
            ORDER BY cs.session_id
            """, enrollmentId, batchId);
    }

    public void saveDraft(
            int enrollmentId,
            BigDecimal assignmentScore,
            BigDecimal examScore,
            BigDecimal attendancePercent,
            BigDecimal totalScore,
            String gradeLetter,
            String result,
            int teacherId) {

        List<Map<String, Object>> existing = jdbc.queryForList("""
            SELECT grade_id, published_at
            FROM grades
            WHERE enrollment_id = ?
            FOR UPDATE
            """, enrollmentId);

        if (existing.isEmpty()) {
            jdbc.update("""
                INSERT INTO grades (
                    enrollment_id,
                    assignment_score,
                    exam_score,
                    attendance_percent,
                    total_score,
                    grade_letter,
                    result,
                    graded_by
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                enrollmentId,
                assignmentScore,
                examScore,
                attendancePercent,
                totalScore,
                gradeLetter,
                result,
                teacherId);

            return;
        }

        if (existing.get(0).get("published_at") != null) {
            throw new IllegalArgumentException(
                    "Published grades cannot be recalculated.");
        }

        int updated = jdbc.update("""
            UPDATE grades
            SET assignment_score = ?,
                exam_score = ?,
                attendance_percent = ?,
                total_score = ?,
                grade_letter = ?,
                result = ?,
                graded_by = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE enrollment_id = ?
              AND published_at IS NULL
            """,
            assignmentScore,
            examScore,
            attendancePercent,
            totalScore,
            gradeLetter,
            result,
            teacherId,
            enrollmentId);

        if (updated != 1) {
            throw new IllegalArgumentException(
                    "Unable to save draft grade.");
        }
    }

    public int publish(int enrollmentId, int teacherId) {
        return jdbc.update("""
            UPDATE grades
            SET published_at = CURRENT_TIMESTAMP,
                graded_by = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE enrollment_id = ?
              AND published_at IS NULL
            """, teacherId, enrollmentId);
    }
}
