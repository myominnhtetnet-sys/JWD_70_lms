package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.Learning_Managnment_System.JWD_70_lms.model.BatchBean.BatchInfo;

@Repository
public class BatchRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 🟢 BatchInfo အတွက် RowMapper (Course Title ပါ တွဲယူပေးသည်)
    private final RowMapper<BatchInfo> batchInfoRowMapper = (rs, rowNum) -> {
        BatchInfo b = new BatchInfo();
        b.setBatchId(rs.getLong("batch_id"));
        b.setCourseId(rs.getLong("course_id"));
        
        try {
            b.setCourseTitle(rs.getString("course_title"));
        } catch (Exception e) {
            // Column မပါလာရင် ကျော်ရန်
        }
        
        b.setBatchCode(rs.getString("batch_code"));
        b.setTitle(rs.getString("title"));
        
        if (rs.getDate("start_date") != null) {
            b.setStartDate(rs.getDate("start_date").toLocalDate());
        }
        if (rs.getDate("end_date") != null) {
            b.setEndDate(rs.getDate("end_date").toLocalDate());
        }
        
        b.setScheduleNote(rs.getString("schedule_note"));
        b.setMaxSeats(rs.getInt("max_seats"));
        b.setStatus(rs.getString("status"));
        b.setCreatedBy(rs.getLong("created_by"));
        
        if (rs.getTimestamp("created_at") != null) {
            b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            b.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        
        return b;
    };

    public List<BatchInfo> findAllBatches() {
        String sql = """
            SELECT b.*, c.title as course_title 
            FROM batches b 
            LEFT JOIN courses c ON b.course_id = c.course_id 
            ORDER BY b.batch_id DESC
        """;
        return jdbcTemplate.query(sql, batchInfoRowMapper);
    }
    public List<BatchBean> findAll() {

        String sql = "SELECT * FROM learning_db.batches ORDER BY batch_id";
           

        List<BatchBean> batches =jdbcTemplate.query(sql,new BatchRowMapper());
        System.out.println("Total Batches: " + batches.size());
        return batches;
    }
    
    public Optional<BatchBean> findById(Integer id) {
        String sql = "SELECT * FROM learning_db.batches where batch_id = ?";

    public BatchInfo findBatchById(Long batchId) {
        String sql = """
            SELECT b.*, c.title as course_title 
            FROM batches b 
            LEFT JOIN courses c ON b.course_id = c.course_id 
            WHERE b.batch_id = ?
        """;
        List<BatchInfo> list = jdbcTemplate.query(sql, batchInfoRowMapper, batchId);
        return list.isEmpty() ? null : list.get(0);
    }

    public int saveBatch(BatchInfo b) {
        String sql = """
            INSERT INTO batches (course_id, batch_code, title, schedule_note, start_date, end_date, max_seats, status, created_by, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1, NOW(), NOW())
        """;
        return jdbcTemplate.update(sql,
            b.getCourseId(),
            b.getBatchCode(),
            b.getTitle(),
            b.getScheduleNote(),
            b.getStartDate(),
            b.getEndDate(),
            b.getMaxSeats(),
            b.getStatus() != null ? b.getStatus() : "UPCOMING"
        );
    }

    public int updateBatch(Long batchId, BatchInfo b) {
        String sql = """
            UPDATE batches 
            SET course_id = ?, batch_code = ?, title = ?, schedule_note = ?, start_date = ?, end_date = ?, max_seats = ?, status = ?, updated_at = NOW()
            WHERE batch_id = ?
        """;
        return jdbcTemplate.update(sql,
            b.getCourseId(),
            b.getBatchCode(),
            b.getTitle(),
            b.getScheduleNote(),
            b.getStartDate(),
            b.getEndDate(),
            b.getMaxSeats(),
            b.getStatus(),
            batchId
        );
    }

    // ကျောင်းသား ရှိမရှိ စစ်ဆေးရန်
    public boolean hasEnrolledStudents(Long batchId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE batch_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, batchId);
        return count != null && count > 0;
    }

    public int deleteBatch(Long batchId) {
        jdbcTemplate.update("DELETE FROM scholarship_applications WHERE batch_id = ?", batchId);
        jdbcTemplate.update("DELETE FROM batch_instructors WHERE batch_id = ?", batchId);
        return jdbcTemplate.update("DELETE FROM batches WHERE batch_id = ?", batchId);
    }

    public int assignInstructor(Long batchId, Long instructorId) {
        String sql = "INSERT IGNORE INTO batch_instructors (batch_id, instructor_id, assigned_at) VALUES (?, ?, NOW())";
        return jdbcTemplate.update(sql, batchId, instructorId);
    }
}