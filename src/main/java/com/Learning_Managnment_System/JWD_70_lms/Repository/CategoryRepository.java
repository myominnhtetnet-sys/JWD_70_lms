package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminBean_category;
import com.Learning_Managnment_System.JWD_70_lms.model.CategoryBean;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseBean;

@Repository
public class CategoryRepository {
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

    // 🟢 AdminBean_category ၏ Field များနှင့် အတိအကျ ကိုက်ညီသော Mapper
    private final RowMapper<AdminBean_category> adminCategoryMapper = (rs, rowNum) -> {
        AdminBean_category cat = new AdminBean_category();
        int catId = rs.getInt("category_id");
        cat.setCategory_id(catId);
        cat.setId(catId); // id နှင့် category_id နှစ်ခုစလုံးကို ထည့်ပေးခြင်း
        
        Integer parentId = rs.getObject("parent_id", Integer.class);
        cat.setParent_id(parentId);
        
        cat.setName(rs.getString("name"));
        cat.setSlug(rs.getString("slug"));
        cat.setDescription(rs.getString("description"));
        
        // Model တွင် is_active သည် String ဖြစ်므로 String ဖြင့် ယူပါမည်
        cat.setIs_active(String.valueOf(rs.getInt("is_active")));
        
        // Timestamp များကိုပါ လိုအပ်ပါက ယူနိုင်သည်
        if (rs.getTimestamp("created_at") != null) {
            cat.setCreated_at(rs.getTimestamp("created_at").toString());
        }
        if (rs.getTimestamp("updated_at") != null) {
            cat.setUpdated_at(rs.getTimestamp("updated_at").toString());
        }
        
        return cat;
    };

    public List<CategoryBean> findAll() {
        String sql = "SELECT category_id, name FROM categories ORDER BY name ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CategoryBean cat = new CategoryBean();
            cat.setCategory_id(rs.getInt("category_id"));
            cat.setName(rs.getString("name"));
            return cat;
        });
    }

    // ==========================================
    // ADMIN CATEGORY CRUD METHODS
    // ==========================================

    public List<AdminBean_category> getAllCategories() {
        String sql = "SELECT category_id, parent_id, name, slug, description, is_active, created_at, updated_at FROM categories ORDER BY category_id DESC";
        return jdbcTemplate.query(sql, adminCategoryMapper);
    }

    public AdminBean_category getCategoryById(int categoryId) {
        String sql = "SELECT category_id, parent_id, name, slug, description, is_active, created_at, updated_at FROM categories WHERE category_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, adminCategoryMapper, categoryId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int saveCategory(AdminBean_category category) {
        String sql = "INSERT INTO categories (parent_id, name, slug, description, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        org.springframework.jdbc.support.GeneratedKeyHolder keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            java.sql.PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            if (category.getParent_id() != null && category.getParent_id() > 0) {
                ps.setInt(1, category.getParent_id());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, category.getName());
            ps.setString(3, category.getSlug());
            ps.setString(4, category.getDescription());
            
            // String ဖြစ်သော is_active ကို int သို့ ပြောင်းထည့်ခြင်း
            int isActiveVal = 1;
            try {
                if (category.getIs_active() != null) {
                    isActiveVal = Integer.parseInt(category.getIs_active());
                }
            } catch (NumberFormatException e) {
                isActiveVal = 1;
            }
            ps.setInt(5, isActiveVal);
            
            return ps;
        }, keyHolder);
        
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : 0;
    }

    public int updateCategory(AdminBean_category category) {
        String sql = "UPDATE categories SET parent_id = ?, name = ?, slug = ?, description = ?, is_active = ?, updated_at = NOW() WHERE category_id = ?";
        Integer parentId = (category.getParent_id() != null && category.getParent_id() > 0) ? category.getParent_id() : null;
        
        int isActiveVal = 1;
        try {
            if (category.getIs_active() != null) {
                isActiveVal = Integer.parseInt(category.getIs_active());
            }
        } catch (NumberFormatException e) {
            isActiveVal = 1;
        }

        return jdbcTemplate.update(sql, parentId, category.getName(), category.getSlug(), category.getDescription(), isActiveVal, category.getCategory_id());
    }

    public int deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        return jdbcTemplate.update(sql, categoryId);
    }

    // ==========================================
    // DASHBOARD STATS METHODS
    // ==========================================

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    public int getActiveCourses() {
        String sql = "SELECT COUNT(*) FROM courses WHERE status = 'PUBLISHED'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // ==========================================
    // COURSE METHODS
    // ==========================================

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

    public void softDelete(Integer id) {
        String sql = "UPDATE courses SET deleted_at = NOW() WHERE course_id = ?";
        jdbcTemplate.update(sql, id);
    }
}