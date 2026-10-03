package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.AssignmentRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;


@Repository
public class AssignmentRepository {
	private final JdbcTemplate jdbcTemplate;

    private final AssignmentRowMapper assignmentRowMapper;

    public AssignmentRepository(
            JdbcTemplate jdbcTemplate,
            AssignmentRowMapper assignmentRowMapper) {

        this.jdbcTemplate = jdbcTemplate;
        this.assignmentRowMapper = assignmentRowMapper;
    }

    // =====================================
    // 1. CREATE - INSERT ASSIGNMENT
    // =====================================

    public int save(AssignmentBean assignment) {

        String sql = """
            INSERT INTO assignments
            (
                batch_id,
                lesson_id,
                title,
                description,
                attachment,
                start_at,
                due_at,
                allow_late_submit,
                late_deadline,
                total_mark,
                pass_mark,
                status,
                created_by
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            );

            ps.setObject(1, assignment.getBatchId());
            ps.setObject(2, assignment.getLessonId());
            ps.setString(3, assignment.getTitle());
            ps.setString(4, assignment.getDescription());
            ps.setString(5, assignment.getAttachment());

            if (assignment.getStartAt() != null) {
                ps.setTimestamp(6,
                    Timestamp.valueOf(assignment.getStartAt()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            if (assignment.getDueAt() != null) {
                ps.setTimestamp(7,
                    Timestamp.valueOf(assignment.getDueAt()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            if (assignment.getAllowLateSubmit() != null) {
                ps.setBoolean(8, assignment.getAllowLateSubmit());
            } else {
                ps.setNull(8, Types.TINYINT);
            }

            if (assignment.getLateDeadline() != null) {
                ps.setTimestamp(9,
                    Timestamp.valueOf(assignment.getLateDeadline()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }

            ps.setBigDecimal(10, assignment.getTotalMark());
            ps.setBigDecimal(11, assignment.getPassMark());
            ps.setString(12, assignment.getStatus());
            ps.setObject(13, assignment.getCreatedBy());

            return ps;

        }, keyHolder);

        return keyHolder.getKey().intValue();
    }


    // =====================================
    // 2. READ ALL - SELECT ALL ASSIGNMENTS
    // =====================================

    public List<AssignmentBean> findAll() {
        String sql = """
            SELECT *
            FROM assignments
            ORDER BY assignment_id DESC
            """;
        return jdbcTemplate.query(sql, assignmentRowMapper);
    }
    
    public List<AssignmentBean> findAll(int page, int size) {
        int offset = (page - 1) * size;
        String sql = """
                SELECT *
                FROM assignments
                ORDER BY assignment_id DESC
                LIMIT ? OFFSET ?
                """;

        return jdbcTemplate.query(sql,assignmentRowMapper,size, offset);
    }
    
    
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM assignments";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
    
    
    
 // =====================================
 // 2. SEARCH & FILTER ASSIGNMENTS
 // =====================================

	public List<AssignmentBean> search(String title, Integer batchId, String status) {

		String sql = """
				SELECT * FROM assignments
				WHERE
				    (? IS NULL OR title LIKE ?)
				    AND (? IS NULL OR batch_id = ?)
				    AND (? IS NULL OR status = ?)
				ORDER BY assignment_id DESC
				""";

		String searchTitle = (title == null || title.isBlank()) ? null : "%" + title.trim() + "%";

		return jdbcTemplate.query(sql, assignmentRowMapper, searchTitle, searchTitle, batchId, batchId, status, status);
	}

    // =====================================
    // 3. READ ONE - FIND BY ID
    // =====================================

    public Optional<AssignmentBean> findById(Integer id) {

        String sql = """
            SELECT *
            FROM assignments
            WHERE assignment_id = ?
            """;

        List<AssignmentBean> result =
                jdbcTemplate.query(
                        sql,
                        assignmentRowMapper,
                        id
                );

        return result.stream().findFirst();
    }

    // =====================================
    // 4. UPDATE - EDIT ASSIGNMENT
    // =====================================

    public int update(AssignmentBean assignment) {

        String sql = """
            UPDATE assignments
            SET
                batch_id = ?,
                lesson_id = ?,
                title = ?,
                description = ?,
                attachment = ?,
                start_at = ?,
                due_at = ?,
                allow_late_submit = ?,
                late_deadline = ?,
                total_mark = ?,
                pass_mark = ?,
                status = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE assignment_id = ?
            """;

        return jdbcTemplate.update(
            sql,
            assignment.getBatchId(),
            assignment.getLessonId(),
            assignment.getTitle(),
            assignment.getDescription(),
            assignment.getAttachment(),
            assignment.getStartAt() == null ? null :
                Timestamp.valueOf(assignment.getStartAt()),
            assignment.getDueAt() == null ? null :
                Timestamp.valueOf(assignment.getDueAt()),
            assignment.getAllowLateSubmit(),
            assignment.getLateDeadline() == null ? null :
                Timestamp.valueOf(assignment.getLateDeadline()),
            assignment.getTotalMark(),
            assignment.getPassMark(),
            assignment.getStatus(),
            assignment.getAssignmentId()
        );
    }


    // =====================================
    // 5. DELETE - DELETE ASSIGNMENT
    // =====================================

    public int deleteById(Integer id) {

        String sql = """
            DELETE FROM assignments
            WHERE assignment_id = ?
            """;

        return jdbcTemplate.update(sql, id);
    }

   
}