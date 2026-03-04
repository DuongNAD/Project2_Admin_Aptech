package com.elearning.admin.dao;

import com.elearning.admin.models.Discussion;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDAO {
    public List<DiscussionDTO> getAll() {
        List<DiscussionDTO> list = new ArrayList<>();
        String sql = "SELECT d.*, u.full_name as user_name, l.title as lesson_title " +
                "FROM discussions d " +
                "JOIN users u ON d.user_id = u.user_id " +
                "JOIN lessons l ON d.lesson_id = l.lesson_id " +
                "ORDER BY d.created_at DESC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                DiscussionDTO dto = new DiscussionDTO();
                Discussion d = new Discussion();
                d.setDiscussionId(rs.getInt("discussion_id"));
                d.setContent(rs.getString("content"));
                d.setCreatedAt(rs.getTimestamp("created_at"));
                dto.discussion = d;
                dto.userName = rs.getString("user_name");
                dto.lessonTitle = rs.getString("lesson_title");
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class DiscussionDTO {
        public Discussion discussion;
        public String userName;
        public String lessonTitle;
    }
}
