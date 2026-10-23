package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.CourseReviewRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.CourseReviewBean;

@Repository
public class CourseReviewRepository {
	
	private final JdbcTemplate jdbcTemplate;

    public CourseReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // List Query
    public List<CourseReviewBean> queryList(String sql, Object... params) {
        return jdbcTemplate.query(sql, new CourseReviewRowMapper(), params);
    }

    // Single Row Query
    public CourseReviewBean querySingle(String sql, Object... params) {
        List<CourseReviewBean> list = jdbcTemplate.query(sql, new CourseReviewRowMapper(), params);
        return list.isEmpty() ? null : list.get(0);
    }

    // Insert / Update / Delete
    public int update(String sql, Object... params) {
    	return jdbcTemplate.update(sql, params);
    }

}
