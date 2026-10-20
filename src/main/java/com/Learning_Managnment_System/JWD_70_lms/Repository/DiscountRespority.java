package com.Learning_Managnment_System.JWD_70_lms.Repository;

import com.Learning_Managnment_System.JWD_70_lms.model.Discount_Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class DiscountRespority{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Discount_Bean> discountRowMapper = (rs, rowNum) -> {
        Discount_Bean discount = new Discount_Bean();
        discount.setDiscountId(rs.getInt("discount_id"));
        discount.setName(rs.getString("name"));
        discount.setDescription(rs.getString("description"));
        discount.setCode(rs.getString("code"));
        discount.setTypeCode(rs.getString("type_code"));
        discount.setDiscountValue(rs.getBigDecimal("discount_value"));
        discount.setMaxDiscountAmount(rs.getBigDecimal("max_discount_amount"));
        discount.setIsActive(rs.getBoolean("is_active"));
        discount.setUsageLimit(rs.getObject("usage_limit") != null ? rs.getInt("usage_limit") : null);
        discount.setUsageLimitPerUser(rs.getInt("usage_limit_per_user"));
        discount.setUsageCount(rs.getInt("usage_count"));
        discount.setCanCombineScholarship(rs.getBoolean("can_combine_scholarship"));
        discount.setCreatedBy(rs.getObject("created_by") != null ? rs.getInt("created_by") : null);

        Timestamp startDate = rs.getTimestamp("starts_at");
        discount.setStartsAt(startDate != null ? startDate.toLocalDateTime() : null);

        Timestamp endDate = rs.getTimestamp("ends_at");
        discount.setEndsAt(endDate != null ? endDate.toLocalDateTime() : null);

        Timestamp createdAt = rs.getTimestamp("created_at");
        discount.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        discount.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

        return discount;
    };

    public List<Discount_Bean> getAllDiscounts() {
        String sql = "SELECT discount_id, name, description, code, type_code, discount_value, " +
                     "max_discount_amount, is_active, usage_limit, usage_limit_per_user, " +
                     "used_count AS usage_count, can_combine_scholarship, created_by, " +
                     "start_date AS starts_at, end_date AS ends_at, created_at, updated_at " +
                     "FROM discounts ORDER BY discount_id DESC";
        return jdbcTemplate.query(sql, discountRowMapper);
    }

    public Discount_Bean getDiscountById(int discountId) {
        String sql = "SELECT discount_id, name, description, code, type_code, discount_value, " +
                     "max_discount_amount, is_active, usage_limit, usage_limit_per_user, " +
                     "used_count AS usage_count, can_combine_scholarship, created_by, " +
                     "start_date AS starts_at, end_date AS ends_at, created_at, updated_at " +
                     "FROM discounts WHERE discount_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, discountRowMapper, discountId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Discount_Bean saveDiscount(Discount_Bean discount) {
        String sql = "INSERT INTO discounts (name, description, code, type_code, discount_value, " +
                     "max_discount_amount, is_active, usage_limit, usage_limit_per_user, " +
                     "can_combine_scholarship, created_by, start_date, end_date, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, discount.getName());
            ps.setString(2, discount.getDescription());
            ps.setString(3, discount.getCode());
            ps.setString(4, discount.getTypeCode());
            ps.setBigDecimal(5, discount.getDiscountValue());
            ps.setBigDecimal(6, discount.getMaxDiscountAmount());
            ps.setBoolean(7, discount.getIsActive() != null ? discount.getIsActive() : true);

            if (discount.getUsageLimit() != null) {
                ps.setInt(8, discount.getUsageLimit());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            ps.setInt(9, discount.getUsageLimitPerUser() != null ? discount.getUsageLimitPerUser() : 1);
            ps.setBoolean(10, discount.getCanCombineScholarship() != null ? discount.getCanCombineScholarship() : false);

            // DEFAULT TO ADMIN USER ID 1 IF NULL
            int createdBy = (discount.getCreatedBy() != null) ? discount.getCreatedBy() : 1;
            ps.setInt(11, createdBy);

            if (discount.getStartsAt() != null) {
                ps.setTimestamp(12, Timestamp.valueOf(discount.getStartsAt()));
            } else {
                ps.setNull(12, java.sql.Types.TIMESTAMP);
            }

            if (discount.getEndsAt() != null) {
                ps.setTimestamp(13, Timestamp.valueOf(discount.getEndsAt()));
            } else {
                ps.setNull(13, java.sql.Types.TIMESTAMP);
            }

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            int newId = keyHolder.getKey().intValue();
            return getDiscountById(newId);
        }
        return null;
    }
    public Discount_Bean updateDiscount(int id, Discount_Bean discount) {
        String sql = "UPDATE discounts SET name = ?, description = ?, code = ?, type_code = ?, " +
                     "discount_value = ?, max_discount_amount = ?, is_active = ?, usage_limit = ?, " +
                     "usage_limit_per_user = ?, can_combine_scholarship = ?, start_date = ?, " +
                     "end_date = ?, updated_at = NOW() WHERE discount_id = ?";

        int rows = jdbcTemplate.update(sql,
                discount.getName(),
                discount.getDescription(),
                discount.getCode(),
                discount.getTypeCode(),
                discount.getDiscountValue(),
                discount.getMaxDiscountAmount(),
                discount.getIsActive() != null ? discount.getIsActive() : true,
                discount.getUsageLimit(),
                discount.getUsageLimitPerUser() != null ? discount.getUsageLimitPerUser() : 1,
                discount.getCanCombineScholarship() != null ? discount.getCanCombineScholarship() : false,
                discount.getStartsAt() != null ? Timestamp.valueOf(discount.getStartsAt()) : null,
                discount.getEndsAt() != null ? Timestamp.valueOf(discount.getEndsAt()) : null,
                id
        );

        return rows > 0 ? getDiscountById(id) : null;
    }

    public boolean deleteDiscount(int discountId) {
        String sql = "DELETE FROM discounts WHERE discount_id = ?";
        return jdbcTemplate.update(sql, discountId) > 0;
    }
}