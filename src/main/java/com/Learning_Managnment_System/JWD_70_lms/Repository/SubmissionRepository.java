package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.SubmissionRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Repository
public class SubmissionRepository {

	private final JdbcTemplate jdbcTemplate;

	public SubmissionRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public int save(SubmissionBean submission) {

		String sql = """
				INSERT INTO submissions
				(
				    assignment_id,
				    enrollment_id,
				    attempt_no,
				    answer_text,
				    attachment,
				    submitted_at,
				    is_late,
				    status
				)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		return jdbcTemplate.update(sql, submission.getAssignmentId(), submission.getEnrollmentId(),
				submission.getAttemptNo(), submission.getAnswerText(), submission.getAttachment(),
				Timestamp.valueOf(submission.getSubmittedAt()), submission.getIsLate(), submission.getStatus());
	}

	public Optional<SubmissionBean> findByAssignmentAndEnrollment(Integer assignmentId, Integer enrollmentId) {

		String sql = """
				SELECT
				    s.*,
				    s.mark AS score,
				    a.title AS assignment_title,
				    a.total_mark AS total_mark,
				    u.full_name AS student_name

				FROM submissions s

				JOIN assignments a
				    ON s.assignment_id = a.assignment_id

				JOIN enrollments e
				    ON s.enrollment_id = e.enrollment_id

				JOIN users u
				    ON e.user_id = u.user_id

				WHERE s.assignment_id = ?
				AND s.enrollment_id = ?

				ORDER BY s.submitted_at DESC
				LIMIT 1
				""";

		List<SubmissionBean> result = jdbcTemplate.query(sql, new SubmissionRowMapper(), assignmentId, enrollmentId);
		
		return result.stream().findFirst();
	}

}