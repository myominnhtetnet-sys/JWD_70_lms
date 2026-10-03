package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.Mapper.BatchRowMapper;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean;

@Repository
public class BatchRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BatchRowMapper batchRowMapper;

    public BatchRepository(
            JdbcTemplate jdbcTemplate,
            BatchRowMapper batchRowMapper) {

        this.jdbcTemplate = jdbcTemplate;
        this.batchRowMapper = batchRowMapper;
    }

    public List<BatchBean> findAll() {

        String sql = """
            SELECT batch_id, batch_code, title
            FROM batches
            ORDER BY batch_id
            """;

        List<BatchBean> batches =
                jdbcTemplate.query(sql, batchRowMapper);

        System.out.println("Total Batches: " + batches.size());

        return batches;
    }
    
    public Optional<BatchBean> findById(Integer id) {

        String sql = """
                SELECT batch_id, batch_code, title
                FROM batches
                WHERE batch_id = ?
                """;

        List<BatchBean> batches = jdbcTemplate.query(sql, batchRowMapper, id);
        return batches.stream().findFirst();
    }
}