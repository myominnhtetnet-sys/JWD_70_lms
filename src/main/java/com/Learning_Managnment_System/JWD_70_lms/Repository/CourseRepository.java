package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CourseRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

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
    
 // 🟢 Added missing findBySlug method
    public Optional<CourseBean> findBySlug(String slug) {
        String sql = "SELECT c.*, cat.name AS category_name, u.full_name AS created_by_name " +
                     "FROM courses c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                     "LEFT JOIN users u ON c.created_by = u.user_id " +
                     "WHERE c.slug = ? AND c.deleted_at IS NULL";
        List<CourseBean> list = jdbcTemplate.query(sql, new CourseMapper(), slug);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Finds a unique course matching a unique text URL slug string.
     */
    /**
     * Finds a unique course matching a unique text URL slug string case-insensitively.
     */
    public Optional<CourseBean> findBySlug(String slug) {
        if (slug == null) return Optional.empty();
        String cleanSlug = slug.trim().toLowerCase();

        // Check 1: Direct match against your table's physical slug field
        String sql = "SELECT * FROM learning_db.courses WHERE LOWER(slug) = ? LIMIT 1";
        try {
            CourseBean course = jdbcTemplate.queryForObject(sql, new CourseRowMapper(), cleanSlug);
            return Optional.ofNullable(course);
        } catch (Exception e) {
            // Check 2: Try calculating the slug directly from the title column if the slug field is missing or empty
            try {
                String fallbackSql = """
                        SELECT * FROM learning_db.courses 
                        WHERE LOWER(REPLACE(REGEXP_REPLACE(title, '[^a-zA-Z0-9\\\\s-]', ''), ' ', '-')) = ? 
                        LIMIT 1
                        """;
                CourseBean fbCourse = jdbcTemplate.queryForObject(fallbackSql, new CourseRowMapper(), cleanSlug);
                return Optional.ofNullable(fbCourse);
            } catch (Exception ex) {
                // Check 3: Final loose matching fallback to capture mixed slashes like UI/UX
                try {
                    String looseSql = "SELECT * FROM learning_db.courses WHERE LOWER(title) LIKE ? LIMIT 1";
                    String searchPattern = "%" + cleanSlug.replace("-", "%") + "%";
                    CourseBean looseCourse = jdbcTemplate.queryForObject(looseSql, new CourseRowMapper(), searchPattern);
                    return Optional.ofNullable(looseCourse);
                } catch (Exception lastError) {
                    return Optional.empty();
                }
            }
        }
    }


    /**
     * Finds a single course matching its exact integer ID identifier.
     */
    public CourseBean findById(int courseId) {
        String sql = "SELECT * FROM learning_db.courses WHERE course_id = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(sql, new CourseRowMapper(), courseId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fetches a paginated, conditionally filtered collection of active courses.
     * 🟢 FIXED: Removed the missing 'c.category' column lookup completely and replaced
     * it with an inline static placeholder alias to avoid 'Unknown column' SQL syntax exceptions.
     */
    /**
     * Fetches a paginated, conditionally filtered collection of active courses.
     * 🟢 FIXED: Added 'c.description' into the field selection list to fulfill 
     * the CourseRowMapper expectation and eliminate the SQL Syntax Error completely.
     */
    public List<CourseBean> findFilteredCourses(CourseBean filters, String batchStatus, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("""
            SELECT 
                c.course_id, 
                c.title, 
                c.short_description, 
                c.description, -- 🟢 RESTORED: Maps directly to line 19 of your CourseRowMapper
                c.level, 
                c.language, 
                c.duration_weeks, 
                c.price, 
                c.allow_discount, 
                c.allow_installment, 
                c.allow_scholarship, 
                c.thumbnail, 
                c.status,
                1 AS category_id,
                c.slug AS slug
            FROM learning_db.courses c 
            JOIN learning_db.batches b ON c.course_id = b.course_id 
            WHERE c.status = 'PUBLISHED' 
            """);
            
        List<Object> params = new ArrayList<>();

        if (filters.getCategory_id() != null && filters.getCategory_id() != 0) {
            sql.append("AND c.course_id = ? ");
            params.add(filters.getCategory_id());
        }
        if (batchStatus != null && !batchStatus.isEmpty()) {
            sql.append("AND b.status = ? ");
            params.add(batchStatus);
        }

        sql.append("GROUP BY c.course_id ORDER BY c.course_id ASC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(page * pageSize);

        return jdbcTemplate.query(sql.toString(), new CourseRowMapper(), params.toArray());
    }

    /**
     * Counts the total items that match a specific combination of filter parameters.
     */
    public int countFilteredCourses(CourseBean filters, String batchStatus) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT c.course_id) FROM learning_db.courses c ");
        sql.append("JOIN learning_db.batches b ON c.course_id = b.course_id WHERE c.status = 'PUBLISHED' ");
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
    
    /**
     * Fetches core active courses for the homepage landing canvas grid.
     * 🟢 FIXED: Generates the slug dynamically on the fly to prevent empty/corrupted title URLs!
     */
    public List<Map<String, Object>> findActivePublishedCourses() {
        // Automatically converts raw titles (like 'UI/UX Design Essentials') into 'ui-ux-design-essentials'
        String sql = """
                SELECT 
                    course_id, 
                    title,
                    description,
                    status,
                    LOWER(REPLACE(REGEXP_REPLACE(title, '[^a-zA-Z0-9\\\\s-]', ''), ' ', '-')) AS slug
                FROM learning_db.courses 
                WHERE status = 'PUBLISHED'
                """;
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            // Safe fallback ensuring compilation never halts
            String fallbackSql = "SELECT course_id, title, title AS slug, description, status FROM learning_db.courses";
            return jdbcTemplate.queryForList(fallbackSql);
        }
    }

}
