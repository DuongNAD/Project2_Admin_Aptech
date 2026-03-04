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
        // For ? students : count enrollments. For ? instructors : count courses
        // created.
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
                user.setCreatedAt(rs.getTimestamp("created_at"));

                int count = rs.getInt("courses_count");
                list.add(new UserWithCount(user, count, "Active")); // hardcode Active as there is no status column
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
