package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.AuditLogBean;

public class AuditLogRowMapper implements RowMapper<AuditLogBean> {

	@Override
    public AuditLogBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuditLogBean log = new AuditLogBean();
        log.setLogId(rs.getLong("log_id"));
        
        // Nullable foreign key user_id
        long userId = rs.getLong("user_id");
        if (!rs.wasNull()) {
            log.setUserId(userId);
        }

        // JOIN စာရင်းမှ full_name ပါလာပါက ယူမည်
        try {
            log.setUserName(rs.getString("user_name"));
        } catch (SQLException e) {
            log.setUserName(null);
        }

        log.setActionType(rs.getString("action_type"));
        log.setTableName(rs.getString("table_name"));
        log.setRecordId(rs.getString("record_id"));
        log.setOldValues(rs.getString("old_values"));
        log.setNewValues(rs.getString("new_values"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setUserAgent(rs.getString("user_agent"));

        if (rs.getTimestamp("created_at") != null) {
            log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return log;
    }

}
