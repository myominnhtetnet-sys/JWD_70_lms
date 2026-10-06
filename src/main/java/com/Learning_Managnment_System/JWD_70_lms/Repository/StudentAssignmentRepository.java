package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.AssignmentRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AssignmentBean;

@Repository
public class StudentAssignmentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final AssignmentRowMapper assignmentRowMapper;

    public StudentAssignmentRepository(
            JdbcTemplate jdbcTemplate,
            AssignmentRowMapper assignmentRowMapper) {

        this.jdbcTemplate = jdbcTemplate;
        this.assignmentRowMapper = assignmentRowMapper;
    }

    public List<AssignmentBean> findAllAssignments() {

        String sql = """
                SELECT *
                FROM assignments
                WHERE status = 'PUBLISHED'
                ORDER BY assignment_id DESC
                """;

        return jdbcTemplate.query(sql, assignmentRowMapper);
    }
    
    public List<Integer> findSubmittedAssignmentIds(Integer enrollmentId) {

        String sql = """
            SELECT DISTINCT assignment_id
            FROM submissions
            WHERE enrollment_id = ?
        """;

        return jdbcTemplate.queryForList(sql,Integer.class,enrollmentId);
    }
    
    public Optional<AssignmentBean> findAssignmentById(Integer assignmentId) {
        String sql = """
                SELECT *
                FROM assignments
                WHERE assignment_id = ?
                AND status = 'PUBLISHED'
                """;

        List<AssignmentBean> result = jdbcTemplate.query(
                sql,assignmentRowMapper,assignmentId);
        return result.stream().findFirst();
    }
    
    public List<Integer> findLateAssignmentIds(Integer enrollmentId) {
        String sql = """
                SELECT DISTINCT assignment_id
                FROM submissions
                WHERE enrollment_id = ?
                AND is_late = TRUE
                """;

        return jdbcTemplate.queryForList(sql,Integer.class,enrollmentId);
    }
}	