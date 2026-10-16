package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    // Constructor Injection အမှန်
    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CategoryBean> findAll() {
        String sql = "SELECT category_id, name FROM categories ORDER BY name ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CategoryBean cat = new CategoryBean();
            cat.setCategory_id(rs.getInt("category_id"));
            cat.setName(rs.getString("name"));
            return cat;
        });
    }
    
 // SAVE NEW COURSE
    public void save(CourseBean course) {
        String generatedSlug = course.getTitle() != null 
                ? course.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "") 
                : "";

        String sql = "INSERT INTO courses (title, slug, category_id, level, language, duration_weeks, price, " +
                     "allow_discount, allow_installment, allow_scholarship, status, short_description, description, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            course.getTitle(),
            generatedSlug,
            course.getCategory_id(),
            course.getLevel(),
            course.getLanguage(),
            course.getDuration_weeks(),
            course.getPrice(),
            course.getAllow_discount(),
            course.getAllow_installment(),
            course.getAllow_scholarship(),
            course.getStatus(),
            course.getShort_description(),
            course.getDescription(),
            course.getCreated_by()
        );
    }

    // SOFT DELETE COURSE
    public void softDelete(Integer id) {
        String sql = "UPDATE courses SET deleted_at = NOW() WHERE course_id = ?";
        jdbcTemplate.update(sql, id);
    }
}