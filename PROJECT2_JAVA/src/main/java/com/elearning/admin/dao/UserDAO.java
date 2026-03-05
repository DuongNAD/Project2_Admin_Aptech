package com.elearning.admin.dao;

import com.elearning.admin.models.User;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public List<UserWithCount> getAllUsersWithCourseCount() {
        List<UserWithCount> list = new ArrayList<>();

        String sql = "SELECT u.*, " +
                "  CASE " +
                "    WHEN u.role = 'student' THEN (SELECT COUNT(*) FROM enrollments e WHERE e.user_id = u.user_id) " +
                "    ELSE 0 " +
                "  END AS courses_count " +
                "FROM users u ORDER BY u.created_at DESC";

        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));

                boolean isActive = rs.getBoolean("is_active");
                user.setStatus(isActive ? "Active" : "Inactive");

                user.setCreatedAt(rs.getTimestamp("created_at"));

                int count = rs.getInt("courses_count");
                list.add(new UserWithCount(user, count, user.getStatus()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fetch admins
        String adminSql = "SELECT admin_id, full_name, email, created_at FROM admins";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(adminSql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                // Admin IDs can conflict with User IDs. For UI purposes we can negate the ID to
                // distinguish.
                user.setUserId(-rs.getInt("admin_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRole("Admin");
                user.setStatus("Active");
                user.setCreatedAt(rs.getTimestamp("created_at"));

                list.add(new UserWithCount(user, 0, user.getStatus()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteUser(int userId) {
        if (userId < 0) {
            // It's an admin. We don't allow deleting admins from this interface.
            return false;
        }
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserStatus(int userId, String status) {
        if (userId < 0) {
            // Block banning admins for safety, or implement logic for admin table
            return false;
        }
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, "Active".equals(status) ? 1 : 0);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getUserCourses(int userId, String role) {
        List<String> courses = new ArrayList<>();
        String sql;
        if ("instructor".equalsIgnoreCase(role)) {
            sql = "SELECT 'None' FROM dual WHERE 1=0";
        } else {
            sql = "SELECT c.title FROM courses c INNER JOIN enrollments e ON c.course_id = e.course_id WHERE e.user_id = ?";
        }

        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static class UserWithCount {
        public User user;
        public int count;
        public String status;

        public UserWithCount(User user, int count, String status) {
            this.user = user;
            this.count = count;
            this.status = status;
        }
    }
}
