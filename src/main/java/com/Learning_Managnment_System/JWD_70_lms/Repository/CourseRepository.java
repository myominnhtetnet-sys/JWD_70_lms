package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CourseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Finds a unique course matching a unique text URL slug string.
     */
    public Optional<CourseBean> findBySlug(String slug) {
        String sql = "SELECT * FROM lms_db.courses WHERE slug = ? LIMIT 1";
        try {
            CourseBean course = jdbcTemplate.queryForObject(sql, new CourseRowMapper(), slug);
            return Optional.ofNullable(course);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Finds a single course matching its exact integer ID identifier.
     */
    public CourseBean findById(int courseId) {
        String sql = "SELECT * FROM lms_db.courses WHERE course_id = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new CourseRowMapper(), courseId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetches a paginated, conditionally filtered collection of active courses.
     */
    public List<CourseBean> findFilteredCourses(CourseBean filters, String batchStatus, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT c.* FROM lms_db.courses c ");
        sql.append("JOIN lms_db.batches b ON c.course_id = b.course_id WHERE c.status = 'PUBLISHED' ");
        List<Object> params = new ArrayList<>();

        if (filters.getCategory_id() != null) {
            sql.append("AND c.category = ? ");
            params.add(filters.getCategory_id());
        }
        if (batchStatus != null && !batchStatus.isEmpty()) {
            sql.append("AND b.status = ? ");
            params.add(batchStatus);
        }

        sql.append("LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(page * pageSize);

        return jdbcTemplate.query(sql.toString(), new CourseRowMapper(), params.toArray());
    }

    /**
     * Counts the total items that match a specific combination of filter parameters.
     */
    public int countFilteredCourses(CourseBean filters, String batchStatus) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT c.course_id) FROM lms_db.courses c ");
        sql.append("JOIN lms_db.batches b ON c.course_id = b.course_id WHERE c.status = 'PUBLISHED' ");
        List<Object> params = new ArrayList<>();

        if (filters.getCategory_id() != null) {
            sql.append("AND c.category = ? ");
            params.add(filters.getCategory_id());
        }
        if (batchStatus != null && !batchStatus.isEmpty()) {
            sql.append("AND b.status = ? ");
            params.add(batchStatus);
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return count != null ? count : 0;
    }
}
