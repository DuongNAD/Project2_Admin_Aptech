package com.elearning.admin.dao;

import com.elearning.admin.models.Enrollment;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    public List<EnrollmentDTO> getAll() {
        List<EnrollmentDTO> list = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name as user_name, c.title as course_title " +
                "FROM enrollments e " +
                "JOIN users u ON e.user_id = u.user_id " +
                "JOIN courses c ON e.course_id = c.course_id " +
                "ORDER BY e.enrolled_at DESC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                EnrollmentDTO dto = new EnrollmentDTO();
                Enrollment e = new Enrollment();
                e.setEnrollmentId(rs.getInt("enrollment_id"));
                e.setEnrolledAt(rs.getTimestamp("enrolled_at"));
                e.setProgressPercent(rs.getDouble("progress_percent"));
                e.setStatus(rs.getString("status"));
                dto.enrollment = e;
                dto.userName = rs.getString("user_name");
                dto.courseTitle = rs.getString("course_title");
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<EnrollmentDTO> getByCourseId(int courseId) {
        List<EnrollmentDTO> list = new ArrayList<>();
        String sql = "SELECT e.*, u.full_name as user_name " +
                "FROM enrollments e " +
                "JOIN users u ON e.user_id = u.user_id " +
                "WHERE e.course_id = ? " +
                "ORDER BY e.enrolled_at DESC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EnrollmentDTO dto = new EnrollmentDTO();
                    Enrollment e = new Enrollment();
                    e.setEnrollmentId(rs.getInt("enrollment_id"));
                    e.setEnrolledAt(rs.getTimestamp("enrolled_at"));
                    e.setProgressPercent(rs.getDouble("progress_percent"));
                    e.setStatus(rs.getString("status"));
                    dto.enrollment = e;
                    dto.userName = rs.getString("user_name");
                    dto.courseTitle = "";
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class EnrollmentDTO {
        public Enrollment enrollment;
        public String userName;
        public String courseTitle;
    }
}
