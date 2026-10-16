package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseRepository {

    private final JdbcTemplate jdbcTemplate;

    public CourseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CourseBean> findAll() {
        String sql = "SELECT c.*, cat.name AS category_name, u.full_name AS created_by_name " +
                     "FROM courses c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                     "LEFT JOIN users u ON c.created_by = u.user_id " +
                     "WHERE c.deleted_at IS NULL " +
                     "ORDER BY c.course_id DESC";
        return jdbcTemplate.query(sql, new CourseMapper());
    }

    public CourseBean findById(Integer id) {
        String sql = "SELECT c.*, cat.name AS category_name, u.full_name AS created_by_name " +
                     "FROM courses c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                     "LEFT JOIN users u ON c.created_by = u.user_id " +
                     "WHERE c.course_id = ? AND c.deleted_at IS NULL";
        List<CourseBean> list = jdbcTemplate.query(sql, new CourseMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void save(CourseBean course) {
        String generatedSlug = course.getTitle() != null 
                ? course.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "") 
                : "";

        String language = (course.getLanguage() != null && !course.getLanguage().trim().isEmpty()) 
                ? course.getLanguage() 
                : "Java";

        String sql = "INSERT INTO courses (title, slug, category_id, level, language, duration_weeks, price, " +
                     "allow_discount, allow_installment, allow_scholarship, status, short_description, description, thumbnail, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            course.getTitle(),
            generatedSlug,
            course.getCategory_id(),
            course.getLevel(),
            language,
            course.getDuration_weeks(),
            course.getPrice(),
            course.getAllow_discount() != null && course.getAllow_discount() == 1, 
            course.getAllow_installment() != null && course.getAllow_installment() == 1, 
            course.getAllow_scholarship() != null && course.getAllow_scholarship() == 1, 
            course.getStatus(),
            course.getShort_description(),
            course.getDescription(),
            course.getThumbnail(),
            course.getCreated_by()
        );
    }

    public void update(CourseBean course) {
        String language = (course.getLanguage() != null && !course.getLanguage().trim().isEmpty()) 
                ? course.getLanguage() 
                : "Java";

        // Thumbnail ပုံအသစ်မပါလာလျှင် အဟောင်းမပျက်အောင် Query ကို ခွဲထုတ်ရေးသားခြင်း
        if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
            String sql = "UPDATE courses SET title = ?, category_id = ?, level = ?, language = ?, " +
                         "duration_weeks = ?, price = ?, allow_discount = ?, allow_installment = ?, " +
                         "allow_scholarship = ?, status = ?, short_description = ?, description = ?, thumbnail = ? " +
                         "WHERE course_id = ?";

            jdbcTemplate.update(sql,
                course.getTitle(),
                course.getCategory_id(),
                course.getLevel(),
                language,
                course.getDuration_weeks(),
                course.getPrice(),
                course.getAllow_discount() != null && course.getAllow_discount() == 1, 
                course.getAllow_installment() != null && course.getAllow_installment() == 1, 
                course.getAllow_scholarship() != null && course.getAllow_scholarship() == 1,
                course.getStatus(),
                course.getShort_description(),
                course.getDescription(),
                course.getThumbnail(),
                course.getCourse_id()
            );
        } else {
            String sql = "UPDATE courses SET title = ?, category_id = ?, level = ?, language = ?, " +
                         "duration_weeks = ?, price = ?, allow_discount = ?, allow_installment = ?, " +
                         "allow_scholarship = ?, status = ?, short_description = ?, description = ? " +
                         "WHERE course_id = ?";

            jdbcTemplate.update(sql,
                course.getTitle(),
                course.getCategory_id(),
                course.getLevel(),
                language,
                course.getDuration_weeks(),
                course.getPrice(),
                course.getAllow_discount() != null && course.getAllow_discount() == 1, 
                course.getAllow_installment() != null && course.getAllow_installment() == 1, 
                course.getAllow_scholarship() != null && course.getAllow_scholarship() == 1,
                course.getStatus(),
                course.getShort_description(),
                course.getDescription(),
                course.getCourse_id()
            );
        }
    }

    public void softDelete(Integer id) {
        String sql = "UPDATE courses SET deleted_at = NOW() WHERE course_id = ?";
        jdbcTemplate.update(sql, id);
    }
}