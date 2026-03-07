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

    public List<RecentEnrollmentDTO> getRecentEnrollments(int limit) {
        List<RecentEnrollmentDTO> list = new ArrayList<>();
        String sql = "SELECT e.enrolled_at, u.full_name as student_name, c.title as course_title, e.progress_percent, e.status "
                +
                "FROM enrollments e " +
                "JOIN users u ON e.user_id = u.user_id " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "ORDER BY e.enrolled_at DESC LIMIT ?";

        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                while (rs.next()) {
                    RecentEnrollmentDTO dto = new RecentEnrollmentDTO();
                    if (rs.getTimestamp("enrolled_at") != null) {
                        dto.enrollmentDate = sdf.format(rs.getTimestamp("enrolled_at"));
                    } else {
                        dto.enrollmentDate = "N/A";
                    }
                    dto.studentName = rs.getString("student_name");
                    dto.courseTitle = rs.getString("course_title");
                    if (dto.courseTitle == null) {
                        dto.courseTitle = "N/A";
                    }
                    dto.progress = String.format("%.0f%%", rs.getDouble("progress_percent"));
                    dto.status = rs.getString("status");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double[] getRevenueByQuarter(int year) {
        double[] quarters = new double[4];
        String sql = "SELECT QUARTER(created_at), SUM(total_amount) FROM orders WHERE YEAR(created_at) = ? AND status = 'completed' GROUP BY QUARTER(created_at)";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int q = rs.getInt(1);
                    double sum = rs.getDouble(2);
                    if (q >= 1 && q <= 4)
                        quarters[q - 1] = sum;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quarters;
    }

    public int[] getNewStudentsByQuarter(int year) {
        int[] quarters = new int[4];
        String sql = "SELECT QUARTER(created_at), COUNT(*) FROM users WHERE role = 'student' AND YEAR(created_at) = ? GROUP BY QUARTER(created_at)";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int q = rs.getInt(1);
                    int count = rs.getInt(2);
                    if (q >= 1 && q <= 4)
                        quarters[q - 1] = count;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quarters;
    }

    public static class DashboardMetrics {
        public double totalRevenue = 0;
        public int totalStudents = 0;
        public int totalCourses = 0;
    }

    public static class RecentEnrollmentDTO {
        public String enrollmentDate;
        public String studentName;
        public String courseTitle;
        public String progress;
        public String status;
    }
}
