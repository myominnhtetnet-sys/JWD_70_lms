package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.AdminCourseRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.AdminCategoryMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminCourseBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean;

@Service
public class AdminCourseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 1. Published ဖြစ်သော Course များကို ယူရန်
    public List<AdminCourseBean> getAllPublishedCourses() {
        String sql = "SELECT c.*, cat.name AS category_name " +
                     "FROM courses c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                     "WHERE c.deleted_at IS NULL " +
                     "ORDER BY c.course_id DESC";
        return jdbcTemplate.query(sql, new AdminCourseRowMapper());
    }

    // 2. Admin Panel အတွက် Course အားလုံးကို ယူရန်
    public List<AdminCourseBean> getAllCoursesForAdmin() {
        String sql = "SELECT c.*, cat.name AS category_name " +
                     "FROM courses c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                     "WHERE c.deleted_at IS NULL " +
                     "ORDER BY c.course_id DESC";
        return jdbcTemplate.query(sql, new AdminCourseRowMapper());
    }
    
    // 3. Categories အားလုံးကို ယူရန်
    public List<CategoryBean> getAllCategories() {
        String sql = "SELECT category_id, name FROM categories";
        return jdbcTemplate.query(sql, new AdminCategoryMapper());
    }

    // 4. Course ID ဖြင့် တစ်ခုချင်းစီ ရှာရန် (View / Edit အတွက်)
    public AdminCourseBean getCourseById(Integer id) {
        String sql = "SELECT c.*, cat.name AS category_name " +
                     "FROM courses c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                     "WHERE c.course_id = ? AND c.deleted_at IS NULL";
        try {
            return jdbcTemplate.queryForObject(sql, new AdminCourseRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // 5. Course အသစ် သိမ်းဆည်းရန် (Create)
    public void saveCourse(AdminCourseBean course) {
        String generatedSlug = course.getTitle() != null 
                ? course.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "") 
                : "";

        String language = (course.getLanguage() != null && !course.getLanguage().trim().isEmpty()) 
                ? course.getLanguage() 
                : "Java";

        String sql = "INSERT INTO courses (title, slug, category_id, level, language, duration_weeks, price, " +
                     "allow_discount, allow_installment, allow_scholarship, status, short_description, thumbnail, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        jdbcTemplate.update(sql,
            course.getTitle(),
            generatedSlug,
            course.getCategoryId(),
            course.getLevel(),
            language,
            course.getDurationWeeks(),
            course.getPrice(),
            course.getAllowDiscount() != null ? course.getAllowDiscount() : false,
            course.getAllowInstallment() != null ? course.getAllowInstallment() : false,
            course.getAllowScholarship() != null ? course.getAllowScholarship() : false,
            course.getStatus(),
            course.getShortDescription(),
            course.getThumbnail()
        );
    }

    // 6. Course အချက်အလက် ပြင်ဆင်ရန် (Update)
    public void updateCourse(AdminCourseBean course) {
        String language = (course.getLanguage() != null && !course.getLanguage().trim().isEmpty()) 
                ? course.getLanguage() 
                : "Java";

        // ပုံအသစ်ပါလာလျှင် Thumbnail ပါ ထည့် update မည်၊ မပါလျှင် Thumbnail ကို ချန်လှပ်မည်
        if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
            String sql = "UPDATE courses SET title = ?, category_id = ?, level = ?, language = ?, " +
                         "duration_weeks = ?, price = ?, allow_discount = ?, allow_installment = ?, " +
                         "allow_scholarship = ?, status = ?, short_description = ?, thumbnail = ?, updated_at = NOW() " +
                         "WHERE course_id = ?";

            jdbcTemplate.update(sql,
                course.getTitle(),
                course.getCategoryId(),
                course.getLevel(),
                language,
                course.getDurationWeeks(),
                course.getPrice(),
                course.getAllowDiscount(),
                course.getAllowInstallment(),
                course.getAllowScholarship(),
                course.getStatus(),
                course.getShortDescription(),
                course.getThumbnail(),
                course.getCourseId()
            );
        } else {
            String sql = "UPDATE courses SET title = ?, category_id = ?, level = ?, language = ?, " +
                         "duration_weeks = ?, price = ?, allow_discount = ?, allow_installment = ?, " +
                         "allow_scholarship = ?, status = ?, short_description = ?, updated_at = NOW() " +
                         "WHERE course_id = ?";

            jdbcTemplate.update(sql,
                course.getTitle(),
                course.getCategoryId(),
                course.getLevel(),
                language,
                course.getDurationWeeks(),
                course.getPrice(),
                course.getAllowDiscount(),
                course.getAllowInstallment(),
                course.getAllowScholarship(),
                course.getStatus(),
                course.getShortDescription(),
                course.getCourseId()
            );
        }
    }

    // 7. Soft Delete ပြုလုပ်ရန်
    public void deleteCourse(int id) {
        String sql = "UPDATE courses SET deleted_at = NOW() WHERE course_id = ?";
        jdbcTemplate.update(sql, id);
    }
}