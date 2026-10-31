package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.Mapper.AuditLogRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AuditLogBean;

@Repository
public class AuditLogRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;


    public List<AuditLogBean> queryList(String sql, Object... params) {
        return jdbcTemplate.query(sql, new AuditLogRowMapper(), params);
    }

    public int update(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

}
