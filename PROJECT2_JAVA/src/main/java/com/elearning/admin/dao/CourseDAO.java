package com.elearning.admin.dao;

import com.elearning.admin.models.Course;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<CourseDTO> getAllCoursesWithDetails() {
        List<CourseDTO> list = new ArrayList<>();
        String sql = "SELECT c.*, cat.name as category_name, u.full_name as instructor_name " +
                "FROM courses c " +
                "LEFT JOIN categories cat ON c.category_id = cat.category_id " +
                "LEFT JOIN admins u ON c.approved_by = u.admin_id " +
                "ORDER BY c.created_at DESC";

        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CourseDTO dto = new CourseDTO();
                Course course = new Course();
                course.setCourseId(rs.getInt("course_id"));
                course.setTitle(rs.getString("title"));
                course.setPrice(rs.getBigDecimal("price"));
                course.setStatus(rs.getString("status"));
                course.setCreatedAt(rs.getTimestamp("created_at"));

                dto.course = course;
                dto.categoryName = rs.getString("category_name");
                if (dto.categoryName == null)
                    dto.categoryName = "N/A";

                dto.instructorName = rs.getString("instructor_name");
                if (dto.instructorName == null)
                    dto.instructorName = "N/A";

                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Course getById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course();
                    course.setCourseId(rs.getInt("course_id"));
                    course.setApprovedBy(rs.getInt("approved_by"));
                    course.setCategoryId(rs.getInt("category_id"));
                    course.setTitle(rs.getString("title"));
                    course.setSubtitle(rs.getString("subtitle"));
                    course.setDescription(rs.getString("description"));
                    course.setPrice(rs.getBigDecimal("price"));
                    course.setSalePrice(rs.getBigDecimal("sale_price"));
                    course.setThumbnailUrl(rs.getString("thumbnail_url"));
                    course.setLanguage(rs.getString("language"));
                    course.setLevel(rs.getString("level"));
                    course.setStatus(rs.getString("status"));
                    course.setCreatedAt(rs.getTimestamp("created_at"));
                    course.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return course;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Course course) {
        String sql = "INSERT INTO courses (approved_by, category_id, title, subtitle, description, " +
                "price, sale_price, thumbnail_url, language, level, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            if (course.getInstructorId() > 0)
                stmt.setInt(1, course.getInstructorId());
            else
                stmt.setNull(1, java.sql.Types.INTEGER);

            if (course.getCategoryId() != null)
                stmt.setInt(2, course.getCategoryId());
            else
                stmt.setNull(2, java.sql.Types.INTEGER);
            stmt.setString(3, course.getTitle());
            stmt.setString(4, course.getSubtitle());
            stmt.setString(5, course.getDescription());
            stmt.setBigDecimal(6, course.getPrice());
            stmt.setBigDecimal(7, course.getSalePrice());
            stmt.setString(8, course.getThumbnailUrl());
            stmt.setString(9, course.getLanguage());
            stmt.setString(10, course.getLevel());
            stmt.setString(11, course.getStatus() != null ? course.getStatus() : "Draft");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        course.setCourseId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Course course) {
        String sql = "UPDATE courses SET category_id = ?, title = ?, subtitle = ?, description = ?, " +
                "price = ?, sale_price = ?, thumbnail_url = ?, language = ?, level = ?, status = ? " +
                "WHERE course_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (course.getCategoryId() != null)
                stmt.setInt(1, course.getCategoryId());
            else
                stmt.setNull(1, java.sql.Types.INTEGER);
            stmt.setString(2, course.getTitle());
            stmt.setString(3, course.getSubtitle());
            stmt.setString(4, course.getDescription());
            stmt.setBigDecimal(5, course.getPrice());
            stmt.setBigDecimal(6, course.getSalePrice());
            stmt.setString(7, course.getThumbnailUrl());
            stmt.setString(8, course.getLanguage());
            stmt.setString(9, course.getLevel());
            stmt.setString(10, course.getStatus());
            stmt.setInt(11, course.getCourseId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class CourseDTO {
        public Course course;
        public String categoryName;
        public String instructorName;
    }
}
