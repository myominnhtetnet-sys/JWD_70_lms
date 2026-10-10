
package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;

@Component
public class SubmissionRowMapper implements RowMapper<SubmissionBean> {

    @Override
    public SubmissionBean mapRow(ResultSet rs, int rowNum) throws SQLException {

        SubmissionBean submission = new SubmissionBean();
        submission.setSubmissionId(rs.getInt("submission_id"));
        submission.setAssignmentId(rs.getInt("assignment_id"));
        submission.setEnrollmentId(rs.getInt("enrollment_id"));
        submission.setAttemptNo(rs.getInt("attempt_no"));
        submission.setAnswerText(rs.getString("answer_text"));
        submission.setAttachment(rs.getString("attachment"));

        if (rs.getTimestamp("submitted_at") != null) {
            submission.setSubmittedAt( rs.getTimestamp("submitted_at").toLocalDateTime());
        }

        submission.setIsLate(rs.getBoolean("is_late"));
        submission.setScore(rs.getBigDecimal("score"));
        submission.setFeedback(rs.getString("feedback"));

        int gradedBy = rs.getInt("graded_by");
        submission.setGradedBy(rs.wasNull() ? null : gradedBy);

        if (rs.getTimestamp("graded_at") != null) {
            submission.setGradedAt(
                rs.getTimestamp("graded_at").toLocalDateTime());
        }

        submission.setStatus(rs.getString("status"));

        if (rs.getTimestamp("created_at") != null) {
            submission.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime());
        }

        if (rs.getTimestamp("updated_at") != null) {
            submission.setUpdatedAt(
                rs.getTimestamp("updated_at").toLocalDateTime());
        }

        submission.setStudentName(rs.getString("student_name"));
        submission.setAssignmentTitle(rs.getString("assignment_title"));
        submission.setTotalMark(rs.getBigDecimal("total_mark"));
        return submission;
    }
}


/*
 * package com.Learning_Managnment_System.JWD_70_lms.Mapper;
 * 
 * import java.sql.ResultSet; import java.sql.SQLException;
 * 
 * import org.springframework.jdbc.core.RowMapper; import
 * org.springframework.stereotype.Component;
 * 
 * import com.Learning_Managnment_System.JWD_70_lms.model.SubmissionBean;
 * 
 * @Component public class SubmissionRowMapper implements
 * RowMapper<SubmissionBean> {
 * 
 * @Override public SubmissionBean mapRow(ResultSet rs, int rowNum)throws
 * SQLException {
 * 
 * SubmissionBean submission = new SubmissionBean();
 * submission.setSubmissionId(rs.getInt("submission_id"));
 * submission.setAssignmentId(rs.getInt("assignment_id"));
 * submission.setEnrollmentId(rs.getInt("enrollment_id"));
 * submission.setAttemptNo(rs.getInt("attempt_no"));
 * submission.setAnswerText(rs.getString("answer_text"));
 * submission.setAttachment(rs.getString("attachment")); if
 * (rs.getTimestamp("submitted_at") != null) { submission.setSubmittedAt(
 * rs.getTimestamp("submitted_at").toLocalDateTime()); }
 * 
 * submission.setIsLate(rs.getBoolean("is_late"));
 * submission.setScore(rs.getBigDecimal("score"));
 * submission.setFeedback(rs.getString("feedback")); int gradedBy =
 * rs.getInt("graded_by"); submission.setGradedBy(rs.wasNull() ? null :
 * gradedBy); if (rs.getTimestamp("graded_at") != null)
 * {submission.setGradedAt(rs.getTimestamp("graded_at").toLocalDateTime()); }
 * 
 * submission.setStatus(rs.getString("status")); if
 * (rs.getTimestamp("created_at") != null)
 * {submission.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); }
 * 
 * if (rs.getTimestamp("updated_at") != null) {
 * submission.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime()); }
 * return submission; } }
 */