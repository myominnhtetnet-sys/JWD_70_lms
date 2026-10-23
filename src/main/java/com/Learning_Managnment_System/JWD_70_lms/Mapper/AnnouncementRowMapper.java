package com.Learning_Managnment_System.JWD_70_lms.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.Learning_Managnment_System.JWD_70_lms.model.AnnouncementBean;

public class AnnouncementRowMapper implements RowMapper<AnnouncementBean> {

	@Override
	public AnnouncementBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		AnnouncementBean announcement = new AnnouncementBean();
        
        announcement.setAnnouncementId(rs.getLong("announcement_id"));
        announcement.setBatchId(rs.getLong("batch_id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setBody(rs.getString("body"));
        announcement.setPostedBy(rs.getLong("posted_by"));
        
        // ResultSet ထဲမှာ ပါလာရင် Map လုပ်ပေးရန် (NULL check လေးပါဝင်သည်)
        try {
            announcement.setBatchTitle(rs.getString("batch_title"));
            announcement.setPostedByName(rs.getString("posted_by_name"));
        } catch (SQLException e) {
            // Column မပါခဲ့ရင် ignore လုပ်မည်
        }

        if (rs.getTimestamp("published_at") != null) {
            announcement.setPublishedAt(rs.getTimestamp("published_at").toLocalDateTime());
        }
        if (rs.getTimestamp("created_at") != null) {
            announcement.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            announcement.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return announcement;
    }

}
