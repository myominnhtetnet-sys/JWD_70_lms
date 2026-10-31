package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.AnnouncementRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.AnnouncementBean;

@Repository
public class AnnouncementRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

    // List Query ပတ်ရန်
    public List<AnnouncementBean> queryList(String sql, Object... params) {
        return jdbcTemplate.query(sql, new AnnouncementRowMapper(), params);
    }

    // Single Row Query ပတ်ရန်
    public AnnouncementBean querySingle(String sql, Object... params) {
        List<AnnouncementBean> list = jdbcTemplate.query(sql, new AnnouncementRowMapper(), params);
        return list.isEmpty() ? null : list.get(0);
    }

    // Insert / Update / Delete အတွက်
    public int update(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

}
