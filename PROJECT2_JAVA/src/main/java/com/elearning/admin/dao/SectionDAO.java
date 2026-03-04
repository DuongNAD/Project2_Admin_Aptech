package com.elearning.admin.dao;

import com.elearning.admin.models.Section;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public List<Section> getSectionsByCourseId(int courseId) {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE course_id = ? ORDER BY order_index ASC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Section s = new Section();
                    s.setSectionId(rs.getInt("section_id"));
                    s.setCourseId(rs.getInt("course_id"));
                    s.setTitle(rs.getString("title"));
                    s.setDescription(rs.getString("description"));
                    s.setOrderIndex(rs.getInt("order_index"));
                    s.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Section section) {
        String sql = "INSERT INTO sections (course_id, title, description, order_index) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, section.getCourseId());
            stmt.setString(2, section.getTitle());
            stmt.setString(3, section.getDescription());
            stmt.setInt(4, section.getOrderIndex());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        section.setSectionId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Section section) {
        String sql = "UPDATE sections SET title = ?, description = ?, order_index = ? WHERE section_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, section.getTitle());
            stmt.setString(2, section.getDescription());
            stmt.setInt(3, section.getOrderIndex());
            stmt.setInt(4, section.getSectionId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
