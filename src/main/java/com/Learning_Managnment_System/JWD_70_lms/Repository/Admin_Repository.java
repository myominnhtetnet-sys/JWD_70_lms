package com.Learning_Managnment_System.JWD_70_lms.Repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
                WHERE u.deleted_at IS NULL
                ORDER BY u.user_id DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            AdminUserBean user = new AdminUserBean();

            user.setUser_id(rs.getInt("user_id"));
            user.setRole_id(rs.getInt("role_id"));
            user.setFull_name(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));

            if (rs.getDate("dob") != null) {
                user.setDob(rs.getDate("dob").toString());
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
        });
    }


    // =========================================================
    // 2. GET USER BY ID
    // =========================================================

    public AdminUserBean getUserById(int id) {

        String sql = """
                SELECT
                    u.user_id,
                    u.role_id,
                    u.full_name,
                    u.email,
                    u.phone,
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
                  AND u.deleted_at IS NULL
                """;

        try {

            return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    AdminUserBean user = new AdminUserBean();

                    user.setUser_id(rs.getInt("user_id"));
                    user.setRole_id(rs.getInt("role_id"));
                    user.setFull_name(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));

                    if (rs.getDate("dob") != null) {
                        user.setDob(rs.getDate("dob").toString());
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

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


 // GET ALL ACTIVE ROLES
    public List<AdminUserBean> getAllRoles() {

        String sql = """
                SELECT role_id, role_name
                FROM roles
                WHERE is_active = 1
                ORDER BY role_id ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            AdminUserBean role = new AdminUserBean();

            role.setRole_id(rs.getInt("role_id"));
            role.setRole_name(rs.getString("role_name"));

            return role;
        });
    }


    // =========================================================
    // 4. CREATE USER
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
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
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
    // 5. UPDATE USER
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
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?
                  AND deleted_at IS NULL
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
    // 6. UPDATE USER PASSWORD
    // =========================================================

    public int updateUserPassword(int userId, String passwordHash) {

        String sql = """
                UPDATE users
                SET
                    password_hash = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?
                  AND deleted_at IS NULL
                """;

        return jdbcTemplate.update(
            sql,
            passwordHash,
            userId
        );
    }


    // =========================================================
    // 7. UPDATE USER STATUS
    // =========================================================

    public int updateUserStatus(int userId, String status) {

        String sql = """
                UPDATE users
                SET
                    status = ?,
                    updated_at = CURRENT_TIMESTAMP
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
    // 8. DELETE USER — SOFT DELETE
    // =========================================================

    public int deleteUser(int id) {

        String sql = """
                UPDATE users
                SET
                    deleted_at = CURRENT_TIMESTAMP,
                    updated_at = CURRENT_TIMESTAMP
                WHERE user_id = ?
                  AND deleted_at IS NULL
                """;

        return jdbcTemplate.update(sql, id);
    }


    // =========================================================
    // 9. COUNT ALL USERS
    // =========================================================

    public int countUsers() {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE deleted_at IS NULL
                """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count == null ? 0 : count;
    }


    // =========================================================
    // 10. COUNT ALL ROLES
    // =========================================================

    public int countRoles() {

        String sql = """
                SELECT COUNT(*)
                FROM roles
                WHERE is_active = 1
                """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count == null ? 0 : count;
    }


    // =========================================================
    // 11. COUNT ACTIVE USERS
    // =========================================================

    public int countActiveUsers() {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE status = 'ACTIVE'
                  AND deleted_at IS NULL
                """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count == null ? 0 : count;
    }


    // =========================================================
    // 12. COUNT INACTIVE USERS
    // =========================================================

    public int countInactiveUsers() {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE status = 'INACTIVE'
                  AND deleted_at IS NULL
                """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count == null ? 0 : count;
    }


    // =========================================================
    // 13. COUNT SUSPENDED USERS
    // =========================================================

    public int countSuspendedUsers() {

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE status = 'SUSPENDED'
                  AND deleted_at IS NULL
                """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class
        );

        return count == null ? 0 : count;
    }


    // =========================================================
    // 14. CHECK EMAIL EXISTS
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
    // 15. CHECK EMAIL EXISTS FOR ANOTHER USER
    // =========================================================

    public boolean emailExistsForAnotherUser(String email, int userId) {

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
    // 16. CHECK PHONE EXISTS
    // =========================================================

    public boolean phoneExists(String phone) {

        if (phone == null || phone.isBlank()) {
            return false;
        }

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE phone = ?
                  AND deleted_at IS NULL
                """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            phone
        );

        return count != null && count > 0;
    }


    // =========================================================
    // 17. CHECK PHONE EXISTS FOR ANOTHER USER
    // =========================================================

    public boolean phoneExistsForAnotherUser(String phone, int userId) {

        if (phone == null || phone.isBlank()) {
            return false;
        }

        String sql = """
                SELECT COUNT(*)
                FROM users
                WHERE phone = ?
                  AND user_id <> ?
                  AND deleted_at IS NULL
                """;

        Integer count = jdbcTemplate.queryForObject(
            sql,
            Integer.class,
            phone,
            userId
        );

        return count != null && count > 0;
    }
 // =========================================================
 // 18. SEARCH AND FILTER USERS
 // =========================================================

 public List<AdminUserBean> searchUsers(
         String keyword,
         String role,
         String status) {

     StringBuilder sql = new StringBuilder("""
             SELECT
                 u.user_id,
                 u.role_id,
                 u.full_name,
                 u.email,
                 u.phone,
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
             WHERE u.deleted_at IS NULL
             """);

     List<Object> params = new java.util.ArrayList<>();

     // Search by name, email, or phone
     if (keyword != null && !keyword.isBlank()) {

         sql.append("""
                 AND (
                     u.full_name LIKE ?
                     OR u.email LIKE ?
                     OR u.phone LIKE ?
                 )
                 """);

         String searchValue = "%" + keyword.trim() + "%";

         params.add(searchValue);
         params.add(searchValue);
         params.add(searchValue);
     }

     // Filter by role ID
     if (role != null && !role.isBlank()) {

         try {
             int roleId = Integer.parseInt(role.trim());

             sql.append(" AND u.role_id = ? ");
             params.add(roleId);

         } catch (NumberFormatException e) {
             // Invalid role ID: return an empty result
             return new java.util.ArrayList<>();
         }
     }

     // Filter by user status
     if (status != null && !status.isBlank()) {

         String normalizedStatus = status.trim().toUpperCase();

         if (!normalizedStatus.equals("ACTIVE")
                 && !normalizedStatus.equals("INACTIVE")
                 && !normalizedStatus.equals("SUSPENDED")) {

             return new java.util.ArrayList<>();
         }

         sql.append(" AND u.status = ? ");
         params.add(normalizedStatus);
     }

     sql.append(" ORDER BY u.user_id DESC ");

     // Execute query without the deprecated overload
     return jdbcTemplate.query(
             sql.toString(),

             ps -> {
                 for (int i = 0; i < params.size(); i++) {
                     ps.setObject(i + 1, params.get(i));
                 }
             },

             (rs, rowNum) -> {

                 AdminUserBean user = new AdminUserBean();

                 user.setUser_id(rs.getInt("user_id"));
                 user.setRole_id(rs.getInt("role_id"));
                 user.setFull_name(rs.getString("full_name"));
                 user.setEmail(rs.getString("email"));
                 user.setPhone(rs.getString("phone"));

                 if (rs.getDate("dob") != null) {
                     user.setDob(rs.getDate("dob").toString());
                 }

                 user.setGender(rs.getString("gender"));
                 user.setAddress(rs.getString("address"));
                 user.setStatus(rs.getString("status"));
                 user.setRole_name(rs.getString("role_name"));

                 if (rs.getTimestamp("created_at") != null) {
                     user.setCreated_at(
                             rs.getTimestamp("created_at")
                                     .toLocalDateTime()
                     );
                 }

                 if (rs.getTimestamp("updated_at") != null) {
                     user.setUpdated_at(
                             rs.getTimestamp("updated_at")
                                     .toLocalDateTime()
                     );
                 }

                 return user;
             }
     );
 }
}