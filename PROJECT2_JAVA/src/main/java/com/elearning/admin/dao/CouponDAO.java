package com.elearning.admin.dao;

import com.elearning.admin.models.Coupon;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CouponDAO {
    public List<Coupon> getAll() {
        List<Coupon> list = new ArrayList<>();
        String sql = "SELECT * FROM coupons ORDER BY coupon_id DESC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Coupon c = new Coupon();
                c.setCouponId(rs.getInt("coupon_id"));
                c.setCode(rs.getString("code"));
                c.setDiscountPercent(rs.getDouble("discount_percent"));
                c.setActive(rs.getBoolean("is_active"));
                c.setExpirationDate(rs.getTimestamp("expiration_date"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Coupon coupon) {
        String sql = "INSERT INTO coupons (code, discount_percent, is_active, expiration_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, coupon.getCode());
            stmt.setDouble(2, coupon.getDiscountPercent());
            stmt.setBoolean(3, coupon.isActive());

            if (coupon.getExpirationDate() != null) {
                stmt.setTimestamp(4, new java.sql.Timestamp(coupon.getExpirationDate().getTime()));
            } else {
                stmt.setNull(4, java.sql.Types.TIMESTAMP);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        coupon.setCouponId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Coupon coupon) {
        String sql = "UPDATE coupons SET code = ?, discount_percent = ?, is_active = ?, expiration_date = ? WHERE coupon_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, coupon.getCode());
            stmt.setDouble(2, coupon.getDiscountPercent());
            stmt.setBoolean(3, coupon.isActive());

            if (coupon.getExpirationDate() != null) {
                stmt.setTimestamp(4, new java.sql.Timestamp(coupon.getExpirationDate().getTime()));
            } else {
                stmt.setNull(4, java.sql.Types.TIMESTAMP);
            }
            stmt.setInt(5, coupon.getCouponId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int couponId) {
        String sql = "DELETE FROM coupons WHERE coupon_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, couponId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
