package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;

@Repository
public class AdminUserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    // ==========================================
    // GET ALL USERS
    // ==========================================

    public List<AdminUserBean> getAllUsers() {

        String sql = """
                SELECT
                    u.user_id,
                    u.role_id,
                    u.full_name,
                    u.email,
                    r.role_name,
                    u.status,
                    u.created_at
                FROM users u
                JOIN roles r
                    ON u.role_id = r.role_id
                ORDER BY u.created_at DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            AdminUserBean user = new AdminUserBean();

            user.setUserId(rs.getInt("user_id"));
            user.setRoleId(rs.getInt("role_id"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setRoleName(rs.getString("role_name"));
            user.setStatus(rs.getString("status"));
            user.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime()
            );

            return user;
        });
    }


    // ==========================================
    // SEARCH USERS
    // ==========================================

    public List<AdminUserBean> searchUsers(String keyword) {

        String sql = """
                SELECT
                    u.user_id,
                    u.role_id,
                    u.full_name,
                    u.email,
                    r.role_name,
                    u.status,
                    u.created_at
                FROM users u
                JOIN roles r
                    ON u.role_id = r.role_id
                WHERE
                    u.full_name LIKE ?
                    OR u.email LIKE ?
                ORDER BY u.created_at DESC
                """;

        String searchKeyword = "%" + keyword + "%";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {

                AdminUserBean user = new AdminUserBean();

                user.setUserId(rs.getInt("user_id"));
                user.setRoleId(rs.getInt("role_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRoleName(rs.getString("role_name"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(
                    rs.getTimestamp("created_at").toLocalDateTime()
                );

                return user;
            },
            searchKeyword,
            searchKeyword
        );
    }


    // ==========================================
    // UPDATE USER STATUS
    // ==========================================

    public int updateStatus(int userId, String status) {

        String sql = """
                UPDATE users
                SET status = ?
                WHERE user_id = ?
                """;

        return jdbcTemplate.update(
            sql,
            status,
            userId
        );
    }


    // ==========================================
    // DELETE USER
    // ==========================================

    public int deleteUser(int userId) {

        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;

        return jdbcTemplate.update(
            sql,
            userId
        );
    }
}