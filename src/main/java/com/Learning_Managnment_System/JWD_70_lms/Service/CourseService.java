package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.BatchRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;

@Service
public class CourseService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // Shared SQL definition constant
    private static final String BASE_BATCH_SQL = "SELECT * FROM lms_db.batches WHERE course_id = ? ORDER BY start_date ASC";

    public List<CourseBean> getFilteredCourses(CourseBean filter, String batchStatus, int page, int pageSize) {
        // 🟢 FIXED: Added explicit table alias "c" to courses table row records
        StringBuilder sql = new StringBuilder("SELECT c.* FROM lms_db.courses c WHERE c.deleted_at IS NULL AND c.status = 'PUBLISHED'");
        List<Object> params = new ArrayList<>();

        buildDynamicQuery(filter, batchStatus, sql, params);

        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(page * pageSize);

        List<CourseBean> courses = jdbcTemplate.query(sql.toString(), new CourseMapper(), params.toArray());

        for (CourseBean course : courses) {
            List<BatchBean> courseBatches = jdbcTemplate.query(BASE_BATCH_SQL, new BatchRowMapper(), course.getCourse_id());
            course.setBatches(courseBatches);
        }

        return courses;
    }

    public int getTotalPagesForFilters(CourseBean filter, String batchStatus, int pageSize) {
        // 🟢 FIXED: Added explicit table alias "c" to courses count tracker rows
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM lms_db.courses c WHERE c.deleted_at IS NULL AND c.status = 'PUBLISHED'");
        List<Object> params = new ArrayList<>();

        buildDynamicQuery(filter, batchStatus, sql, params);

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
                List<BatchBean> courseBatches = jdbcTemplate.query(BASE_BATCH_SQL, new BatchRowMapper(), course.getCourse_id());
                course.setBatches(courseBatches);
            }
            return course;
        } catch (EmptyResultDataAccessException e) {
            return null; 
        }
    }

    public List<CourseBean> showAllCourses() {
        String sql = "SELECT * FROM lms_db.courses WHERE deleted_at IS NULL AND status = 'PUBLISHED'";
        
        // 1. Fetch flat course records safely
        List<CourseBean> courses = jdbcTemplate.query(sql, new CourseMapper());
        
        // 2. 🟢 FIXED: Loop and fetch ALL batches using the service's working jdbcTemplate
        for (CourseBean course : courses) {
            List<BatchBean> courseBatches = jdbcTemplate.query(BASE_BATCH_SQL, new BatchRowMapper(), course.getCourse_id());
            course.setBatches(courseBatches);
        }
        
        return courses;
    }



    private void buildDynamicQuery(CourseBean filter, String batchStatus, StringBuilder sql, List<Object> params) {
        if (filter != null) {
            // 🟢 FIXED: Appended "c." table alias indicators to all flat columns to match the main query scopes
            if (filter.getTitle() != null && !filter.getTitle().trim().isEmpty()) {
                sql.append(" AND LOWER(c.title) LIKE ?");
                params.add("%" + filter.getTitle().trim().toLowerCase() + "%");
            }
            if (filter.getLevel() != null && !filter.getLevel().trim().isEmpty()) {
                sql.append(" AND c.level = ?");
                params.add(filter.getLevel().trim());
            }
            if (filter.getPrice() != null) {
                sql.append(" AND c.price <= ?");
                params.add(filter.getPrice());
            }
            if (filter.getAllow_discount() != null) {
                sql.append(" AND c.allow_discount = ?");
                params.add(filter.getAllow_discount());
            }
            if (filter.getAllow_installment() != null) {
                sql.append(" AND c.allow_installment = ?");
                params.add(filter.getAllow_installment());
            }
            if (filter.getAllow_scholarship() != null) {
                sql.append(" AND c.allow_scholarship = ?");
                params.add(filter.getAllow_scholarship());
            }
        }

        // 🟢 FIXED: Uses explicit aliases "b.course_id = c.course_id" to force an accurate relational inner check loop step!
        if (batchStatus != null && !batchStatus.trim().isEmpty()) {
            sql.append(" AND EXISTS (SELECT 1 FROM lms_db.batches b WHERE b.course_id = c.course_id AND b.status = ?)");
            params.add(batchStatus.trim());
        }
    }
}
