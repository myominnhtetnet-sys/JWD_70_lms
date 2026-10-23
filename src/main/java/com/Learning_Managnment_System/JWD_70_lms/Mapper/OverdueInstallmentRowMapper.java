package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.InstallmentBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OverdueInstallmentRowMapper implements RowMapper<InstallmentBean> {

    @Override
    public InstallmentBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        InstallmentBean installment = new InstallmentBean();

        // Main table fields (CamelCase setters)
        installment.setScheduleId(rs.getInt("schedule_id"));
        installment.setEnrollmentId(rs.getInt("enrollment_id"));
        installment.setRuleItemId(rs.getInt("rule_item_id"));
        installment.setInstallmentNumber(rs.getInt("installment_number"));

        // LocalDate conversion for due_date
        java.sql.Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            installment.setDueDate(dueDate.toLocalDate());
        }

        installment.setAmountDue(rs.getBigDecimal("amount_due"));
        installment.setLateFee(rs.getBigDecimal("late_fee"));
        installment.setPaidAmount(rs.getBigDecimal("paid_amount"));
        installment.setStatus(rs.getString("status"));

        // Timestamps
        java.sql.Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) {
            installment.setPaidAt(paidAt.toLocalDateTime());
        }

        java.sql.Timestamp reminderSentAt = rs.getTimestamp("reminder_sent_at");
        if (reminderSentAt != null) {
            installment.setReminderSentAt(reminderSentAt.toLocalDateTime());
        }

        // Optional Timestamps (created_at, updated_at if queried)
        try {
            java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                installment.setCreatedAt(createdAt.toLocalDateTime());
            }
        } catch (SQLException e) { }

        try {
            java.sql.Timestamp updatedAt = rs.getTimestamp("updated_at");
            if (updatedAt != null) {
                installment.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException e) { }

        // Overdue သီးသန့် Joining / Extra fields များ
        try {
            installment.setStudentName(rs.getString("student_name"));
        } catch (SQLException e) { }

        try {
            installment.setStudentEmail(rs.getString("student_email"));
        } catch (SQLException e) { }

        try {
            installment.setCourseName(rs.getString("course_name"));
        } catch (SQLException e) { }

        try {
            installment.setBatchName(rs.getString("batch_name"));
        } catch (SQLException e) { }

        try {
            installment.setDaysOverdue(rs.getInt("days_overdue"));
        } catch (SQLException e) { }

        try {
            installment.setOverdueStatus(rs.getString("overdue_status"));
        } catch (SQLException e) { }

        return installment;
    }
}