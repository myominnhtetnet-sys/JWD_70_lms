package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import com.Learning_Managnment_System.JWD_70_lms.model.InstallmentBean;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OverdueInstallmentRowMapper implements RowMapper<InstallmentBean> {

    @Override
    public InstallmentBean mapRow(ResultSet rs, int rowNum) throws SQLException {
        InstallmentBean installment = new InstallmentBean();

        // Main table fields
        installment.setSchedule_id(rs.getInt("schedule_id"));
        installment.setEnrollment_id(rs.getInt("enrollment_id"));
        installment.setRule_item_id(rs.getInt("rule_item_id"));
        installment.setInstallment_number(rs.getInt("installment_number"));

        // LocalDate conversion for due_date
        java.sql.Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            installment.setDue_date(dueDate.toLocalDate());
        }

        installment.setAmount_due(rs.getBigDecimal("amount_due"));
        installment.setLate_fee(rs.getBigDecimal("late_fee"));
        installment.setPaid_amount(rs.getBigDecimal("paid_amount"));
        installment.setStatus(rs.getString("status"));

        // Timestamps
        java.sql.Timestamp paidAt = rs.getTimestamp("paid_at");
        if (paidAt != null) {
            installment.setPaid_at(paidAt.toLocalDateTime());
        }

        java.sql.Timestamp reminderSentAt = rs.getTimestamp("reminder_sent_at");
        if (reminderSentAt != null) {
            installment.setReminder_sent_at(reminderSentAt.toLocalDateTime());
        }

        // Overdue သီးသန့် Joining / Extra fields များ (Query ထဲမှာ တွက်ချက်ထားသည်များ)
        try {
            installment.setStudent_name(rs.getString("student_name"));
        } catch (SQLException e) { }

        try {
            installment.setStudent_email(rs.getString("student_email"));
        } catch (SQLException e) { }

        try {
            installment.setCourse_name(rs.getString("course_name"));
        } catch (SQLException e) { }

        try {
            installment.setBatch_name(rs.getString("batch_name"));
        } catch (SQLException e) { }

        try {
            installment.setDays_overdue(rs.getInt("days_overdue"));
        } catch (SQLException e) { }

        try {
            installment.setOverdue_status(rs.getString("overdue_status"));
        } catch (SQLException e) { }

        return installment;
    }
}