package com.Learning_Managnment_System.JWD_70_lms.Repository;

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
    // GET ALL USERS
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
                    u.status,
                    u.created_at,
                    u.updated_at,
                    r.role_name
                FROM users u
                LEFT JOIN roles r
                    ON u.role_id = r.role_id
                ORDER BY u.user_id DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    AdminUserBean user = new AdminUserBean();

                    user.setUser_id(rs.getInt("user_id"));
                    user.setRole_id(rs.getInt("role_id"));

                    user.setFull_name(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setPassword_hash(rs.getString("password_hash"));

                    if (rs.getDate("dob") != null) {
                        user.setDob(
                                rs.getDate("dob").toString()
                        );
                    }

                    user.setGender(rs.getString("gender"));
                    user.setAddress(rs.getString("address"));
                    user.setStatus(rs.getString("status"));

                    user.setRole_name(rs.getString("role_name"));

                    if (rs.getTimestamp("created_at") != null) {
                        user.setCreated_at(
                                rs.getTimestamp("created_at").toLocalDateTime()
                        );
                    }

                    if (rs.getTimestamp("updated_at") != null) {
                        user.setUpdated_at(
                                rs.getTimestamp("updated_at").toLocalDateTime()
                        );
                    }

                    return user;
                }
        );
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    public AdminUserBean getUserById(int id) {

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
                    u.status,
                    u.created_at,
                    u.updated_at,
                    r.role_name
                FROM users u
                LEFT JOIN roles r
                    ON u.role_id = r.role_id
                WHERE u.user_id = ?
                """;

        List<AdminUserBean> list = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    AdminUserBean user = new AdminUserBean();

                    user.setUser_id(rs.getInt("user_id"));
                    user.setRole_id(rs.getInt("role_id"));

                    user.setFull_name(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setPassword_hash(rs.getString("password_hash"));

                    if (rs.getDate("dob") != null) {
                        user.setDob(
                                rs.getDate("dob").toString()
                        );
                    }

                    user.setGender(rs.getString("gender"));
                    user.setAddress(rs.getString("address"));
                    user.setStatus(rs.getString("status"));

                    user.setRole_name(rs.getString("role_name"));

                    if (rs.getTimestamp("created_at") != null) {
                        user.setCreated_at(
                                rs.getTimestamp("created_at").toLocalDateTime()
                        );
                    }

                    if (rs.getTimestamp("updated_at") != null) {
                        user.setUpdated_at(
                                rs.getTimestamp("updated_at").toLocalDateTime()
                        );
                    }

                    return user;
                },
                id
        );

        return list.isEmpty() ? null : list.get(0);
    }


    // =========================================================
    // CREATE USER
    // =========================================================

    public int createUser(AdminUserBean user) {

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
                    ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    NOW(),
                    NOW()
                )
                """;

        return jdbcTemplate.update(
                sql,
                user.getRole_id(),
                user.getFull_name(),
                user.getEmail(),
                user.getPhone(),
                user.getPassword_hash(),
                user.getDob(),
                user.getGender(),
                user.getAddress(),
                user.getStatus()
        );
    }


    // =========================================================
    // UPDATE USER
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
                """;

        return jdbcTemplate.update(
                sql,
                user.getRole_id(),
                user.getFull_name(),
                user.getEmail(),
                user.getPhone(),
                user.getDob(),
                user.getGender(),
                user.getAddress(),
                user.getStatus(),
                user.getUser_id()
        );
    }


    // =========================================================
    // DELETE USER
    // =========================================================

    public int deleteUser(int id) {

        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }


    // =========================================================
    // COUNT USERS
    // =========================================================

    public int countUsers() {

        String sql = """
                SELECT COUNT(*)
                FROM users
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class
        );
    }


    // =========================================================
    // COUNT ROLES
    // =========================================================

    public int countRoles() {

        String sql = """
                SELECT COUNT(*)
                FROM roles
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class
        );
    }


    // =========================================================
    // COUNT ACTIVE USERS
    // =========================================================

    public int countActiveUsers() {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE status = 'ACTIVE'
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class
        );
    }


    // =========================================================
    // COUNT INACTIVE USERS
    // =========================================================

    public int countInactiveUsers() {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE status = 'INACTIVE'
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Integer.class
        );
    }
}