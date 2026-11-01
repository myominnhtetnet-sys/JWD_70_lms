package com.Learning_Managnment_System.JWD_70_lms.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TeacherCourseBuilderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Fetch course details by ID
    public Map<String, Object> getCourseById(int courseId) {
        String sql = "SELECT course_id, title, slug, level, status, short_description FROM courses WHERE course_id = ?";
        List <Map<String, Object>> result = jdbcTemplate.queryForList(sql, courseId);
        return result.isEmpty() ? null : result.get(0);
    }

    // Fetch modules for a specific course
    public List <Map<String, Object>> getModulesByCourseId(int courseId) {
        String sql = "SELECT module_id, course_id, title, sort_order, " +
                     "(SELECT COUNT(*) FROM lessons l WHERE l.module_id = cm.module_id) AS lesson_count " +
                     "FROM course_modules cm WHERE cm.course_id = ? ORDER BY sort_order ASC";
        return jdbcTemplate.queryForList(sql, courseId);
    }

    // Fetch lessons for a specific module
    public List <Map<String, Object>> getLessonsByModuleId(int moduleId) {
    	String sql = "SELECT lesson_id, module_id, title, content_type AS lesson_type, duration_min AS duration_mins, video_url, content, is_preview, status, sort_order " +
                "FROM lessons WHERE module_id = ? ORDER BY sort_order ASC";
   return jdbcTemplate.queryForList(sql, moduleId);
}
 // Fetch materials for a specific course by joining through modules and lessons (or if course_id exists directly, adjust accordingly)
    public List <Map<String, Object>> getMaterialsByCourseId(int courseId) {
        // NOTE: If your lesson_materials links via lesson_id -> module_id -> course_id:
        String sql = "SELECT lm.material_id, lm.lesson_id, lm.title, lm.file_path AS file_url, lm.file_type, lm.created_at " +
                     "FROM lesson_materials lm " +
                     "JOIN lessons l ON lm.lesson_id = l.lesson_id " +
                     "JOIN course_modules cm ON l.module_id = cm.module_id " +
                     "WHERE cm.course_id = ?";
        return jdbcTemplate.queryForList(sql, courseId);
    }
 // Insert a new module for a course
    public void insertModule(int courseId, String title, String description, int sortOrder) {
        String sql = "INSERT INTO course_modules (course_id, title, description, sort_order) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, courseId, title, description, sortOrder);
    }

    // Insert a new lesson into a module
    public void insertLesson(int moduleId, String title, String contentType, String content, String videoUrl, int durationMin, int sortOrder, int isPreview, String status) {
        String sql = "INSERT INTO lessons (module_id, title, content_type, content, video_url, duration_min, sort_order, is_preview, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, moduleId, title, contentType, content, videoUrl, durationMin, sortOrder, isPreview, status);
    }
 // Delete all lessons in a module first, then delete the module itself
    public void deleteModule(int moduleId) {
        String deleteLessonsSql = "DELETE FROM lessons WHERE module_id = ?";
        jdbcTemplate.update(deleteLessonsSql, moduleId);

        String deleteModuleSql = "DELETE FROM course_modules WHERE module_id = ?";
        jdbcTemplate.update(deleteModuleSql, moduleId);
    }
 // Check if a lesson with the same sort order already exists in the module
    public boolean lessonSortOrderExists(int moduleId, int sortOrder) {
        String sql = "SELECT COUNT(*) FROM lessons WHERE module_id = ? AND sort_order = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, moduleId, sortOrder);
        return count != null && count > 0;
    }
 // Delete a lesson by its ID
    public void deleteLesson(int lessonId) {
        String sql = "DELETE FROM lessons WHERE lesson_id = ?";
        jdbcTemplate.update(sql, lessonId);
    }
 // Update an existing lesson
    public void updateLesson(int lessonId, String title, String contentType, String content, String videoUrl, int durationMin, int sortOrder, int isPreview, String status) {
        String sql = "UPDATE lessons SET title = ?, content_type = ?, content = ?, video_url = ?, duration_min = ?, sort_order = ?, is_preview = ?, status = ? WHERE lesson_id = ?";
        jdbcTemplate.update(sql, title, contentType, content, videoUrl, durationMin, sortOrder, isPreview, status, lessonId);
    }
    // Insert uploaded course material (associating with the first lesson of the first module as a course-level fallback if required by schema)
    public void insertMaterial(int courseId, String title, String fileType, String filePath) {
        // Find the first available lesson in this course to satisfy the lesson_id foreign key constraint
        String findLessonSql = "SELECT l.lesson_id FROM lessons l " +
                               "JOIN course_modules cm ON l.module_id = cm.module_id " +
                               "WHERE cm.course_id = ? LIMIT 1";
        
        List<Integer> lessonIds = jdbcTemplate.queryForList(findLessonSql, Integer.class, courseId);
        
        if (lessonIds.isEmpty()) {
            throw new RuntimeException("You must create at least one module and lesson before uploading materials!");
        }
        
        int lessonId = lessonIds.get(0);

        // Insert into lesson_materials using auto-increment for material_id
        String sql = "INSERT INTO lesson_materials (lesson_id, title, file_type, file_path) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, lessonId, title, fileType, filePath);
    }
 // Delete course material and its local file
    public void deleteMaterial(int materialId) {
        String getUrlSql = "SELECT file_path FROM lesson_materials WHERE material_id = ?";
        try {
            String filePath = jdbcTemplate.queryForObject(getUrlSql, String.class, materialId);
            if (filePath != null && filePath.startsWith("/uploads/materials/")) {
                java.io.File file = new java.io.File("uploads/materials/" + filePath.substring("/uploads/materials/".length()));
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            // Proceed to delete DB record even if physical file is missing
        }

        String sql = "DELETE FROM lesson_materials WHERE material_id = ?";
        jdbcTemplate.update(sql, materialId);
    }
}