package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseBuilderMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.InstructorRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.ModuleRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.LessonRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.MaterialRowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.CourseBuilderBean;
import com.Learning_Managnment_System.JWD_70_lms.model.InstructorBean;
import com.Learning_Managnment_System.JWD_70_lms.model.ModuleBean;
import com.Learning_Managnment_System.JWD_70_lms.model.LessonBean;
import com.Learning_Managnment_System.JWD_70_lms.model.MaterialBean;

@Repository
public class CourseBuilderRepository {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<CourseBuilderBean> findAllCourses() {
        String sql = "SELECT c.*, cat.name as category_name FROM courses c LEFT JOIN categories cat ON c.category_id = cat.category_id ORDER BY c.course_id DESC";
        return jdbcTemplate.query(sql, new CourseBuilderMapper());
    }

    public CourseBuilderBean findById(Long courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        List<CourseBuilderBean> courses = jdbcTemplate.query(sql, new CourseBuilderMapper(), courseId);
        return courses.isEmpty() ? null : courses.get(0);
    }

    public List<InstructorBean> findAllInstructors() {
        String sql = "SELECT user_id, full_name FROM users WHERE role_id = 2 ORDER BY full_name ASC";
        return jdbcTemplate.query(sql, new InstructorRowMapper());
    }

    // --- 1. COURSE INSTRUCTORS ---
    public List<InstructorBean> findInstructorsByCourseId(Long courseId) {
        String sql = "SELECT u.user_id, u.full_name FROM course_instructors ci JOIN users u ON ci.instructor_id = u.user_id WHERE ci.course_id = ?";
        return jdbcTemplate.query(sql, new InstructorRowMapper(), courseId);
    }

    public void saveInstructorAssignment(Long courseId, Long instructorId) {
        jdbcTemplate.update("INSERT INTO course_instructors (course_id, instructor_id, is_primary, assigned_at) VALUES (?, ?, 1, NOW())", courseId, instructorId);
    }

    public void deleteInstructorAssignment(Long courseId, Long instructorId) {
        jdbcTemplate.update("DELETE FROM course_instructors WHERE course_id = ? AND instructor_id = ?", courseId, instructorId);
    }

    // --- 2. COURSE MODULES ---
    public List<ModuleBean> findModulesByCourseId(Long courseId) {
        return jdbcTemplate.query("SELECT module_id, course_id, title FROM course_modules WHERE course_id = ? ORDER BY module_id ASC", new ModuleRowMapper(), courseId);
    }

    public void saveModule(Long courseId, String title) {
        jdbcTemplate.update("INSERT INTO course_modules (course_id, title, sort_order, created_at, updated_at) VALUES (?, ?, 1, NOW(), NOW())", courseId, title);
    }

    public void deleteModule(Long moduleId) {
        jdbcTemplate.update("DELETE FROM course_modules WHERE module_id = ?", moduleId);
    }

    // --- 3. LESSONS ---
    public List<LessonBean> findLessonsByCourseId(Long courseId) {
        String sql = "SELECT l.lesson_id, l.title FROM lessons l JOIN course_modules cm ON l.module_id = cm.module_id WHERE cm.course_id = ?";
        return jdbcTemplate.query(sql, new LessonRowMapper(), courseId);
    }

    public void saveLesson(Long courseId, String title, String videoUrl) {
        List<ModuleBean> modules = jdbcTemplate.query("SELECT module_id, course_id, title FROM course_modules WHERE course_id = ? LIMIT 1", new ModuleRowMapper(), courseId);
        if (!modules.isEmpty()) {
            jdbcTemplate.update("INSERT INTO lessons (module_id, title, video_url, content_type, sort_order, status, created_at, updated_at) VALUES (?, ?, ?, 'VIDEO', 1, 'PUBLISHED', NOW(), NOW())", modules.get(0).getModuleId(), title, videoUrl);
        }
    }

    public void deleteLesson(Long lessonId) {
        jdbcTemplate.update("DELETE FROM lessons WHERE lesson_id = ?", lessonId);
    }

    // --- 4. MATERIALS ---
    public List<MaterialBean> findMaterialsByCourseId(Long courseId) {
        String sql = "SELECT lm.material_id, lm.lesson_id, lm.title, lm.file_path FROM lesson_materials lm JOIN lessons l ON lm.lesson_id = l.lesson_id JOIN course_modules cm ON l.module_id = cm.module_id WHERE cm.course_id = ?";
        return jdbcTemplate.query(sql, new MaterialRowMapper(), courseId);
    }

    public void saveMaterial(Long courseId, String title, String filePath) {
        List<LessonBean> lessons = jdbcTemplate.query("SELECT l.lesson_id, l.title FROM lessons l JOIN course_modules cm ON l.module_id = cm.module_id WHERE cm.course_id = ? LIMIT 1", new LessonRowMapper(), courseId);
        if (!lessons.isEmpty()) {
            jdbcTemplate.update("INSERT INTO lesson_materials (lesson_id, title, file_path, file_type, file_size_kb, created_at) VALUES (?, ?, ?, 'PDF', 1024, NOW())", lessons.get(0).getLessonId(), title, filePath);
        }
    }

    public void deleteMaterial(Long materialId) {
        jdbcTemplate.update("DELETE FROM lesson_materials WHERE material_id = ?", materialId);
    }

    // --- COURSE CRUD ---
    public void save(CourseBuilderBean course, int currentUserId) {
        String sql = """
            INSERT INTO courses (category_id, title, slug, short_description, description, level, language, price, duration_weeks, status, allow_discount, allow_installment, allow_scholarship, created_by, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        """;
        jdbcTemplate.update(sql, course.getCategoryId(), course.getTitle(), course.getSlug(), course.getShortDescription(), course.getDescription(), course.getLevel(), course.getLanguage(), course.getPrice(), course.getDurationWeeks(), course.getStatus(), course.getAllowDiscount() ? 1 : 0, course.getAllowInstallment() ? 1 : 0, course.getAllowScholarship() ? 1 : 0, currentUserId);
    }

    public void update(Long courseId, CourseBuilderBean course) {
        String sql = """
            UPDATE courses SET category_id = ?, title = ?, slug = ?, short_description = ?, description = ?, level = ?, language = ?, price = ?, duration_weeks = ?, status = ?, allow_discount = ?, allow_installment = ?, allow_scholarship = ?, updated_at = NOW() 
            WHERE course_id = ?
        """;
        jdbcTemplate.update(sql, course.getCategoryId(), course.getTitle(), course.getSlug(), course.getShortDescription(), course.getDescription(), course.getLevel(), course.getLanguage(), course.getPrice(), course.getDurationWeeks(), course.getStatus(), course.getAllowDiscount() ? 1 : 0, course.getAllowInstallment() ? 1 : 0, course.getAllowScholarship() ? 1 : 0, courseId);
    }

    public void delete(Long courseId) {
        Integer enrollmentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM enrollments e JOIN batches b ON e.batch_id = b.batch_id WHERE b.course_id = ?", Integer.class, courseId);
        if (enrollmentCount != null && enrollmentCount > 0) {
            throw new RuntimeException("Cannot delete this course because students are actively enrolled in it.");
        }
        jdbcTemplate.update("DELETE FROM course_modules WHERE course_id = ?", courseId);
        jdbcTemplate.update("DELETE FROM batches WHERE course_id = ?", courseId);
        jdbcTemplate.update("DELETE FROM courses WHERE course_id = ?", courseId);
    }
}