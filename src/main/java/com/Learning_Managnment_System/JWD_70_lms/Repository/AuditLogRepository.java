package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.AuditLogRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AuditLogBean;

@Repository
public class AuditLogRepository {
	
	private final JdbcTemplate jdbcTemplate;

    public AuditLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AuditLogBean> queryList(String sql, Object... params) {
        return jdbcTemplate.query(sql, new AuditLogRowMapper(), params);
    }

    public int update(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

}
