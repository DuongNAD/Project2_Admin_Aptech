package com.elearning.admin.dao;

import com.elearning.admin.models.Lesson;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LessonDAO {

    public List<Lesson> getLessonsBySectionId(int sectionId) {
        List<Lesson> list = new ArrayList<>();
        String sql = "SELECT * FROM lessons WHERE section_id = ? ORDER BY order_index ASC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Lesson l = new Lesson();
                    l.setLessonId(rs.getInt("lesson_id"));
                    l.setSectionId(rs.getInt("section_id"));
                    l.setTitle(rs.getString("title"));
                    l.setContent(rs.getString("content"));
                    l.setVideoUrl(rs.getString("video_url"));

                    int duration = rs.getInt("duration_seconds");
                    if (!rs.wasNull()) {
                        l.setDurationSeconds(duration);
                    }

                    l.setOrderIndex(rs.getInt("order_index"));
                    l.setPreview(rs.getBoolean("is_preview"));
                    l.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Lesson lesson) {
        String sql = "INSERT INTO lessons (section_id, title, content, video_url, duration_seconds, order_index, is_preview) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, lesson.getSectionId());
            stmt.setString(2, lesson.getTitle());
            stmt.setString(3, lesson.getContent());
            stmt.setString(4, lesson.getVideoUrl());

            if (lesson.getDurationSeconds() != null) {
                stmt.setInt(5, lesson.getDurationSeconds());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.setInt(6, lesson.getOrderIndex());
            stmt.setBoolean(7, lesson.isPreview());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        lesson.setLessonId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Lesson lesson) {
        String sql = "UPDATE lessons SET title = ?, content = ?, video_url = ?, duration_seconds = ?, order_index = ?, is_preview = ? WHERE lesson_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, lesson.getTitle());
            stmt.setString(2, lesson.getContent());
            stmt.setString(3, lesson.getVideoUrl());

            if (lesson.getDurationSeconds() != null) {
                stmt.setInt(4, lesson.getDurationSeconds());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setInt(5, lesson.getOrderIndex());
            stmt.setBoolean(6, lesson.isPreview());
            stmt.setInt(7, lesson.getLessonId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int lessonId) {
        String sql = "DELETE FROM lessons WHERE lesson_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lessonId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
