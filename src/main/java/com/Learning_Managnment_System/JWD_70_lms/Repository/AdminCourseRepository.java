package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.AdminCourseRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AdminCourseBean;

@Repository
public class AdminCourseRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<AdminCourseBean> findAll() {
        String sql = "SELECT c.*, cat.name AS category_name " +
                     "FROM courses c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                     "WHERE c.deleted_at IS NULL " +
                     "ORDER BY c.course_id DESC";
        return jdbcTemplate.query(sql, new AdminCourseRowMapper());
    }

    public AdminCourseBean findById(Integer id) {
        String sql = "SELECT c.*, cat.name AS category_name " +
                     "FROM courses c " +
                     "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                     "WHERE c.course_id = ? AND c.deleted_at IS NULL";
        List<AdminCourseBean> list = jdbcTemplate.query(sql, new AdminCourseRowMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public void softDelete(Integer id) {
        String sql = "UPDATE courses SET deleted_at = NOW() WHERE course_id = ?";
        jdbcTemplate.update(sql, id);
    }
}