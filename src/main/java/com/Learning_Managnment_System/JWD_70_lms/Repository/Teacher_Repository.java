package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Teacher_Repository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Get active batches assigned to this instructor[cite: 1]
    public List<Map<String, Object>> getBatchesByInstructor(int instructorId) {
        String sql = "SELECT b.batch_id, b.batch_code, b.title, b.start_date, b.end_date, " +
                     "b.schedule_note, b.max_seats, b.status, c.title AS course_title " +
                     "FROM batches b " +
                     "JOIN batch_instructors bi ON b.batch_id = bi.batch_id " +
                     "JOIN courses c ON b.course_id = c.course_id " +
                     "WHERE bi.instructor_id = ?";
        return jdbcTemplate.queryForList(sql, instructorId);
    }

    // Get courses assigned to this instructor[cite: 1]
    public List<Map<String, Object>> getCoursesByInstructor(int instructorId) {
        String sql = "SELECT c.course_id, c.title, c.slug, c.short_description, c.level, c.status " +
                     "FROM courses c " +
                     "JOIN course_instructors ci ON c.course_id = ci.course_id " +
                     "WHERE ci.instructor_id = ?";
        return jdbcTemplate.queryForList(sql, instructorId);
    }
}