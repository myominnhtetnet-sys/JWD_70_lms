package com.Learning_Managnment_System.JWD_70_lms.Service;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.BatchRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Repository.CourseRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final JdbcTemplate jdbcTemplate;

    // Shared SQL definition constant
    private static final String BASE_BATCH_SQL = "SELECT * FROM batches WHERE course_id = ? ORDER BY start_date ASC";

    public CourseService(CourseRepository courseRepository, JdbcTemplate jdbcTemplate) {
        this.courseRepository = courseRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // 🟢 Fix: Return courses with batches attached
    public List<CourseBean> getAllCourses() {
        String sql = "SELECT * FROM courses WHERE deleted_at IS NULL";
        List<CourseBean> courses = jdbcTemplate.query(sql, new CourseMapper());
        
        for (CourseBean course : courses) {
            List<BatchBean> courseBatches = jdbcTemplate.query(BASE_BATCH_SQL, new BatchRowMapper(), course.getCourse_id());
            course.setBatches(courseBatches);
        }
        
        return courses;
    }

    public List<CourseBean> getFilteredCourses(CourseBean filter, String batchStatus, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT c.* FROM courses c WHERE c.deleted_at IS NULL AND c.status = 'PUBLISHED'");
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
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM courses c WHERE c.deleted_at IS NULL AND c.status = 'PUBLISHED'");
        List<Object> params = new ArrayList<>();

        buildDynamicQuery(filter, batchStatus, sql, params);

        Integer totalRows = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        if (totalRows == null || totalRows == 0) {
            return 1;
        }
        
        return (int) Math.ceil((double) totalRows / pageSize);
    }

    public CourseBean getCourseById(Integer id) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try {
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
        String sql = "SELECT * FROM courses WHERE deleted_at IS NULL AND status = 'PUBLISHED'";
        List<CourseBean> courses = jdbcTemplate.query(sql, new CourseMapper());
        
        for (CourseBean course : courses) {
            List<BatchBean> courseBatches = jdbcTemplate.query(BASE_BATCH_SQL, new BatchRowMapper(), course.getCourse_id());
            course.setBatches(courseBatches);
        }
        
        return courses;
    }

    public void saveCourse(CourseBean course) {
        // အကယ်၍ Repository ထဲမှာ save method မရှိသေးပါက သို့မဟုတ် repository ကိုသုံးလိုပါက
        courseRepository.save(course);
    }

    public void updateCourse(CourseBean course) {
        courseRepository.update(course);
    }

    public void deleteCourse(int id) {
        // Soft delete သို့မဟုတ် Hard delete (deleted_at ကို update လုပ်ခြင်း သို့မဟုတ် row ဖျက်ခြင်း)
        String sql = "UPDATE courses SET deleted_at = NOW() WHERE course_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private void buildDynamicQuery(CourseBean filter, String batchStatus, StringBuilder sql, List<Object> params) {
        if (filter != null) {
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

        if (batchStatus != null && !batchStatus.trim().isEmpty()) {
            sql.append(" AND EXISTS (SELECT 1 FROM batches b WHERE b.course_id = c.course_id AND b.status = ?)");
            params.add(batchStatus.trim());
        }
    }
}