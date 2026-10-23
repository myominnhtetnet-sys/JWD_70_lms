package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.AuditLogRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.AuditLogBean;

@Service
public class AuditLogService {
	
	private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    // Audit Log အားလုံးကို Created At အလိုက် နောက်ဆုံးလုပ်ခဲ့တာကို အပေါ်ဆုံးထား၍ ဆွဲထုတ်မည်
    public List<AuditLogBean> getAllAuditLogs() {
        String sql = """
            SELECT 
                al.log_id, al.user_id, u.full_name AS user_name,
                al.action_type, al.table_name, al.record_id,
                al.old_values, al.new_values, al.ip_address, al.user_agent, al.created_at
            FROM audit_logs al
            LEFT JOIN users u ON al.user_id = u.user_id
            ORDER BY al.created_at DESC
        """;
        return repository.queryList(sql);
    }

    // Action Type (e.g., 'CREATE', 'UPDATE', 'DELETE') အလိုက် Filter လုပ်ရန်
    public List<AuditLogBean> getLogsByActionType(String actionType) {
        String sql = """
            SELECT 
                al.log_id, al.user_id, u.full_name AS user_name,
                al.action_type, al.table_name, al.record_id,
                al.old_values, al.new_values, al.ip_address, al.user_agent, al.created_at
            FROM audit_logs al
            LEFT JOIN users u ON al.user_id = u.user_id
            WHERE al.action_type = ?
            ORDER BY al.created_at DESC
        """;
        return repository.queryList(sql, actionType);
    }

    // Log အသစ် သိမ်းဆည်းပေးသည့် Helper Method
    public boolean saveAuditLog(Long userId, String actionType, String tableName, String recordId, 
                               String oldValues, String newValues, String ipAddress, String userAgent) {
        String sql = """
            INSERT INTO audit_logs (user_id, action_type, table_name, record_id, old_values, new_values, ip_address, user_agent, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;
        return repository.update(sql, userId, actionType, tableName, recordId, oldValues, newValues, ipAddress, userAgent) > 0;
    }

}
