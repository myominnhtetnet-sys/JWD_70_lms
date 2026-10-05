package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.AdminUserBean;

@Repository
public class Admin_Repository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    // =========================================================
    // 1. GET ALL USERS
    // =========================================================

    public List<AdminUserBean> getAllUsers() {

        String sql = """
            SELECT
                u.user_id,
                u.role_id,
                u.full_name,
                u.email,
                u.phone,
                u.password_hash,
                u.dob,
                u.gender,
                u.address,
                r.role_name,
                u.status,
                u.created_at,
                u.updated_at
            FROM users u
            JOIN roles r
                ON u.role_id = r.role_id
            WHERE u.deleted_at IS NULL
            ORDER BY u.user_id DESC
            """;

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapUser(rs)
        );
    }


    // =========================================================
    // 2. SEARCH USERS
    // =========================================================

    public List<AdminUserBean> searchUsers(String keyword) {

        String sql = """
            SELECT
                u.user_id,
                u.role_id,
                u.full_name,
                u.email,
                u.phone,
                u.password_hash,
                u.dob,
                u.gender,
                u.address,
                r.role_name,
                u.status,
                u.created_at,
                u.updated_at
            FROM users u
            JOIN roles r
                ON u.role_id = r.role_id
            WHERE u.deleted_at IS NULL
            AND (
                u.full_name LIKE ?
                OR u.email LIKE ?
                OR r.role_name LIKE ?
                OR u.status LIKE ?
                OR CAST(u.user_id AS CHAR) LIKE ?
            )
            ORDER BY u.user_id DESC
            """;

        String search = "%" + keyword.trim() + "%";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapUser(rs),
            search,
            search,
            search,
            search,
            search
        );
    }


    // =========================================================
    // 3. GET USER BY ID
    // =========================================================

    public AdminUserBean getUserById(int userId) {

        String sql = """
            SELECT
                u.user_id,
                u.role_id,
                u.full_name,
                u.email,
                u.phone,
                u.password_hash,
                u.dob,
                u.gender,
                u.address,
                r.role_name,
                u.status,
                u.created_at,
                u.updated_at
            FROM users u
            JOIN roles r
                ON u.role_id = r.role_id
            WHERE u.user_id = ?
            AND u.deleted_at IS NULL
            """;

        List<AdminUserBean> users = jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapUser(rs),
            userId
        );

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }


    // =========================================================
    // 4. CHECK EMAIL EXISTS
    // =========================================================

    public boolean emailExists(String email) {

        String sql = """
            SELECT COUNT(*)
            FROM users
            WHERE email = ?
            AND deleted_at IS NULL
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            email
        );

        return count != null && count > 0;
    }


    // =========================================================
    // 5. CHECK EMAIL EXISTS FOR OTHER USER
    // =========================================================

    public boolean emailExistsForOtherUser(String email, int userId) {

        String sql = """
            SELECT COUNT(*)
            FROM users
            WHERE email = ?
            AND user_id <> ?
            AND deleted_at IS NULL
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            email,
            userId
        );

        return count != null && count > 0;
    }


    // =========================================================
    // 6. ADD USER
    // =========================================================

    public int addUser(AdminUserBean user) {

        String sql = """
            INSERT INTO users
            (
                role_id,
                full_name,
                email,
                phone,
                password_hash,
                dob,
                gender,
                address,
                status,
                created_at,
                updated_at
            )
            VALUES
            (
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                ?,
                NOW(),
                NOW()
            )
            """;

        return jdbcTemplate.update(
            sql,
            user.getRoleId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.getPasswordHash(),
            user.getDob(),
            user.getGender(),
            user.getAddress(),
            user.getStatus()
        );
    }


    // =========================================================
    // 7. UPDATE USER
    // =========================================================

    public int updateUser(AdminUserBean user) {

        String sql = """
            UPDATE users
            SET
                role_id = ?,
                full_name = ?,
                email = ?,
                phone = ?,
                dob = ?,
                gender = ?,
                address = ?,
                status = ?,
                updated_at = NOW()
            WHERE user_id = ?
            AND deleted_at IS NULL
            """;

        return jdbcTemplate.update(
            sql,
            user.getRoleId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhone(),
            user.getDob(),
            user.getGender(),
            user.getAddress(),
            user.getStatus(),
            user.getUserId()
        );
    }


    // =========================================================
    // 8. UPDATE PASSWORD
    // =========================================================

    public int updatePassword(int userId, String password) {

        String sql = """
            UPDATE users
            SET
                password_hash = ?,
                updated_at = NOW()
            WHERE user_id = ?
            AND deleted_at IS NULL
            """;

        return jdbcTemplate.update(
            sql,
            password,
            userId
        );
    }


    // =========================================================
    // 9. UPDATE USER STATUS
    // =========================================================

    public int updateStatus(int userId, String status) {

        String sql = """
            UPDATE users
            SET
                status = ?,
                updated_at = NOW()
            WHERE user_id = ?
            AND deleted_at IS NULL
            """;

        return jdbcTemplate.update(
            sql,
            status,
            userId
        );
    }


    // =========================================================
    // 10. SOFT DELETE USER
    // =========================================================

    public int deleteUser(int userId) {

        String sql = """
            UPDATE users
            SET
                deleted_at = NOW(),
                updated_at = NOW()
            WHERE user_id = ?
            AND deleted_at IS NULL
            """;

        return jdbcTemplate.update(
            sql,
            userId
        );
    }


    // =========================================================
    // 11. DASHBOARD STATISTICS
    // =========================================================

    public int getTotalUsers() {

        String sql = """
            SELECT COUNT(*)
            FROM users
            WHERE deleted_at IS NULL
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count != null ? count : 0;
    }


    public int getTotalStudents() {

        String sql = """
            SELECT COUNT(*)
            FROM users u
            JOIN roles r
                ON u.role_id = r.role_id
            WHERE r.role_name = 'Student'
            AND u.deleted_at IS NULL
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count != null ? count : 0;
    }


    public int getTotalTeachers() {

        String sql = """
            SELECT COUNT(*)
            FROM users u
            JOIN roles r
                ON u.role_id = r.role_id
            WHERE r.role_name = 'Teacher'
            AND u.deleted_at IS NULL
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count != null ? count : 0;
    }


    public int getTotalAdmins() {

        String sql = """
            SELECT COUNT(*)
            FROM users u
            JOIN roles r
                ON u.role_id = r.role_id
            WHERE r.role_name = 'Admin'
            AND u.deleted_at IS NULL
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count != null ? count : 0;
    }


    // =========================================================
    // 12. ACTIVE COURSES
    // =========================================================

    public int getActiveCourses() {

        String sql = """
            SELECT COUNT(*)
            FROM courses
            WHERE status = 'ACTIVE'
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count != null ? count : 0;
    }


    // =========================================================
    // 13. TOTAL ENROLLMENTS
    // =========================================================

    public int getTotalEnrollments() {

        String sql = """
            SELECT COUNT(*)
            FROM enrollments
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count != null ? count : 0;
    }


    // =========================================================
    // 14. ACTIVE BATCHES
    // =========================================================

    public int getActiveBatches() {

        String sql = """
            SELECT COUNT(*)
            FROM batches
            WHERE status = 'ACTIVE'
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count != null ? count : 0;
    }


    // =========================================================
    // 15. UPCOMING BATCHES
    // =========================================================

    public int getUpcomingBatches() {

        String sql = """
            SELECT COUNT(*)
            FROM batches
            WHERE start_date > CURDATE()
            """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count != null ? count : 0;
    }


    // =========================================================
    // COMMON ROW MAPPER
    // =========================================================

    private AdminUserBean mapUser(ResultSet rs) throws java.sql.SQLException {

        AdminUserBean user = new AdminUserBean();

        user.setUserId(rs.getInt("user_id"));
        user.setRoleId(rs.getInt("role_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setDob(rs.getDate("dob") != null
                ? rs.getDate("dob").toLocalDate()
                : null);
        user.setGender(rs.getString("gender"));
        user.setAddress(rs.getString("address"));
        user.setRoleName(rs.getString("role_name"));
        user.setStatus(rs.getString("status"));

        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime()
            );
        }

        if (rs.getTimestamp("updated_at") != null) {
            user.setUpdatedAt(
                rs.getTimestamp("updated_at").toLocalDateTime()
            );
        }

        return user;
    }
}