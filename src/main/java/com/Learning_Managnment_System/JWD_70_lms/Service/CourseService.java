package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.BatchMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;

@Service
public class CourseService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // Shared SQL definition constant
    private static final String BASE_BATCH_SQL = "SELECT * FROM lms_db.batches WHERE course_id = ? ORDER BY start_date ASC";

    public List<CourseBean> getFilteredCourses(CourseBean filter, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM lms_db.courses WHERE deleted_at IS NULL AND status = 'PUBLISHED'");
        List<Object> params = new ArrayList<>();

        buildDynamicQuery(filter, sql, params);

        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(page * pageSize);

        // 🟢 INSTANTIATED INLINE: Matches your exact 'new UserRowMapper()' parameter style
        List<CourseBean> courses = jdbcTemplate.query(sql.toString(), new CourseMapper(), params.toArray());

        // Enrich rows with dynamic batch collection arrays sequentially
        for (CourseBean course : courses) {
            List<BatchBean> courseBatches = jdbcTemplate.query(BASE_BATCH_SQL, new BatchMapper(), course.getCourse_id());
            course.setBatches(courseBatches);
        }

        return courses;
    }

    public int getTotalPagesForFilters(CourseBean filter, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM lms_db.courses WHERE deleted_at IS NULL AND status = 'PUBLISHED'");
        List<Object> params = new ArrayList<>();

        buildDynamicQuery(filter, sql, params);

        Integer totalRows = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        if (totalRows == null || totalRows == 0) {
            return 1;
        }
        
        return (int) Math.ceil((double) totalRows / pageSize);
    }

    public CourseBean getCourseById(Integer id) {
        String sql = "SELECT * FROM lms_db.courses WHERE course_id = ?";
        try {
            // 🟢 INSTANTIATED INLINE: Matches your exact 'new UserRowMapper()' parameter style
            CourseBean course = jdbcTemplate.queryForObject(sql, new CourseMapper(), id);
            if (course != null) {
                List<BatchBean> courseBatches = jdbcTemplate.query(BASE_BATCH_SQL, new BatchMapper(), course.getCourse_id());
                course.setBatches(courseBatches);
            }
            return course;
        } catch (EmptyResultDataAccessException e) {
            return null; 
        }
    }

    public List<CourseBean> showAllCourses() {
        String sql = "SELECT * FROM lms_db.courses WHERE deleted_at IS NULL AND status = 'PUBLISHED'";
        // 🟢 INSTANTIATED INLINE: Matches your exact 'new UserRowMapper()' parameter style
        return jdbcTemplate.query(sql, new CourseMapper());
    }

    private void buildDynamicQuery(CourseBean filter, StringBuilder sql, List<Object> params) {
        if (filter == null) {
            return;
        }
        
        if (filter.getTitle() != null && !filter.getTitle().trim().isEmpty()) {
            sql.append(" AND LOWER(title) LIKE ?");
            params.add("%" + filter.getTitle().trim().toLowerCase() + "%");
        }
        if (filter.getLevel() != null && !filter.getLevel().trim().isEmpty()) {
            sql.append(" AND level = ?");
            params.add(filter.getLevel().trim());
        }
        if (filter.getPrice() != null) {
            sql.append(" AND price <= ?");
            params.add(filter.getPrice());
        }
        if (filter.getAllow_discount() != null) {
            sql.append(" AND allow_discount = ?");
            params.add(filter.getAllow_discount());
        }
        if (filter.getAllow_installment() != null) {
            sql.append(" AND allow_installment = ?");
            params.add(filter.getAllow_installment());
        }
        if (filter.getAllow_scholarship() != null) {
            sql.append(" AND allow_scholarship = ?");
            params.add(filter.getAllow_scholarship());
        }
    }
}
