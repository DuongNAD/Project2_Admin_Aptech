package com.elearning.admin.dao;

import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public DashboardMetrics getMetrics() {
        DashboardMetrics metrics = new DashboardMetrics();
        try (Connection conn = DatabaseConnect.getConnection()) {
            // total revenue
            try (PreparedStatement stmt = conn
                    .prepareStatement("SELECT SUM(total_amount) FROM orders WHERE status = 'completed'");
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    metrics.totalRevenue = rs.getDouble(1);
                }
            }

            // total students
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'student'");
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    metrics.totalStudents = rs.getInt(1);
                }
            }

            // total courses
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM courses");
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    metrics.totalCourses = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return metrics;
    }

    public List<RecentOrderDTO> getRecentOrders(int limit) {
        List<RecentOrderDTO> list = new ArrayList<>();
        String sql = "SELECT o.order_id, u.full_name as student_name, o.total_amount, o.status, " +
                "(SELECT c.title FROM order_details od JOIN courses c ON od.course_id = c.course_id WHERE od.order_id = o.order_id LIMIT 1) as course_title "
                +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id " +
                "ORDER BY o.created_at DESC LIMIT ?"; 
                
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RecentOrderDTO dto = new RecentOrderDTO();
                    dto.orderId = "ORD-" + rs.getInt("order_id");
                    dto.studentName = rs.getString("student_name");
                    dto.courseTitle = rs.getString("course_title");
                    if (dto.courseTitle == null) {
                        dto.courseTitle = "N/A";
                    }
                    dto.total = "$" + rs.getDouble("total_amount");
                    dto.status = rs.getString("status");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class DashboardMetrics {
        public double totalRevenue = 0;
        public int totalStudents = 0;
        public int totalCourses = 0;
    }

    public static class RecentOrderDTO {
        public String orderId;
        public String studentName;
        public String courseTitle;
        public String total;
        public String status;
    }
}
