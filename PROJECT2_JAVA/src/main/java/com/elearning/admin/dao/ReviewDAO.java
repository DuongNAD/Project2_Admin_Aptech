package com.elearning.admin.dao;

import com.elearning.admin.models.Review;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public List<ReviewDTO> getAll() {
        List<ReviewDTO> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as user_name, c.title as course_title " +
                "FROM reviews r " +
                "JOIN users u ON r.user_id = u.user_id " +
                "JOIN courses c ON r.course_id = c.course_id " +
                "ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ReviewDTO dto = new ReviewDTO();
                Review r = new Review();
                r.setReviewId(rs.getInt("review_id"));
                r.setRating(rs.getInt("rating"));
                r.setComment(rs.getString("comment"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                dto.review = r;
                dto.userName = rs.getString("user_name");
                dto.courseTitle = rs.getString("course_title");
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class ReviewDTO {
        public Review review;
        public String userName;
        public String courseTitle;
    }
}
