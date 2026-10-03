package com.Learning_Managnment_System.JWD_70_lms.Mapper;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;

@Component
public class BatchRowMapper implements RowMapper<BatchBean> {

    @Override
    public BatchBean mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        BatchBean batch = new BatchBean();

        batch.setBatchId(rs.getInt("batch_id"));
        batch.setBatchCode(rs.getString("batch_code"));
        batch.setTitle(rs.getString("title"));

        return batch;
    }
}