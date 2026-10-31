package com.Learning_Managnment_System.JWD_70_lms.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Learning_Managnment_System.JWD_70_lms.Repository.AnnouncementRepository;
import com.Learning_Managnment_System.JWD_70_lms.model.AnnouncementBean;

@Service
public class AnnouncementService {
	
	@Autowired
    private AnnouncementRepository repository;

    // 1. Get All Announcements
    public List<AnnouncementBean> getAllAnnouncements() {
        String sql = """
            SELECT 
                a.announcement_id, a.batch_id, b.title AS batch_title,
                a.title, a.body, a.posted_by, u.full_name AS posted_by_name,
                a.published_at, a.created_at, a.updated_at
            FROM announcements a
            LEFT JOIN batches b ON a.batch_id = b.batch_id
            LEFT JOIN users u ON a.posted_by = u.user_id
            ORDER BY a.created_at DESC
        """;
        return repository.queryList(sql);
    }

    // 2. Get Single Announcement by ID
    public AnnouncementBean getAnnouncementById(Long id) {
        String sql = """
            SELECT 
                a.announcement_id, a.batch_id, b.title AS batch_title,
                a.title, a.body, a.posted_by, u.full_name AS posted_by_name,
                a.published_at, a.created_at, a.updated_at
            FROM announcements a
            LEFT JOIN batches b ON a.batch_id = b.batch_id
            LEFT JOIN users u ON a.posted_by = u.user_id
            WHERE a.announcement_id = ?
        """;
        return repository.querySingle(sql, id);
    }

    // 3. Create Announcement
    public boolean createAnnouncement(AnnouncementBean a) {
        String sql = """
            INSERT INTO announcements (batch_id, title, body, posted_by, published_at, created_at, updated_at)
            VALUES (?, ?, ?, ?, NOW(), NOW(), NOW())
        """;
        return repository.update(sql, a.getBatchId(), a.getTitle(), a.getBody(), a.getPostedBy()) > 0;
    }

    // 4. Update Announcement
    public boolean updateAnnouncement(Long id, AnnouncementBean a) {
        String sql = """
            UPDATE announcements 
            SET batch_id = ?, title = ?, body = ?, updated_at = NOW()
            WHERE announcement_id = ?
        """;
        return repository.update(sql, a.getBatchId(), a.getTitle(), a.getBody(), id) > 0;
    }

    // 5. Delete Announcement
    public boolean deleteAnnouncement(Long id) {
        String sql = "DELETE FROM announcements WHERE announcement_id = ?";
        return repository.update(sql, id) > 0;
    }

    // 6. Get Latest Announcements with Limit (Notifications Dropdown အတွက်)
    public List<AnnouncementBean> getLatestAnnouncements(int limit) {
        String sql = """
            SELECT 
                a.announcement_id, a.batch_id, b.title AS batch_title,
                a.title, a.body, a.posted_by, u.full_name AS posted_by_name,
                a.published_at, a.created_at, a.updated_at
            FROM announcements a
            LEFT JOIN batches b ON a.batch_id = b.batch_id
            LEFT JOIN users u ON a.posted_by = u.user_id
            ORDER BY a.created_at DESC
            LIMIT ?
        """;
        return repository.queryList(sql, limit);
    }

}