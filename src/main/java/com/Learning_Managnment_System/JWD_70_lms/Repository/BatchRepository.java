package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List; 
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.BatchRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;

@Repository
public class BatchRepository {

	@Autowired
    JdbcTemplate jdbcTemplate;
    BatchRowMapper batchRowMapper;

  

    public List<BatchBean> findAll() {

        String sql = "SELECT * FROM lms_db.batches ORDER BY batch_id";
           

        List<BatchBean> batches =jdbcTemplate.query(sql,new BatchRowMapper());
        System.out.println("Total Batches: " + batches.size());
        return batches;
    }
    
    public Optional<BatchBean> findById(Integer id) {
        String sql = "SELECT * FROM lms_db.batches where batch_id = ?";

        List<BatchBean> batches = jdbcTemplate.query(sql, new BatchRowMapper(), id);
        return batches.stream().findFirst();
    }
}