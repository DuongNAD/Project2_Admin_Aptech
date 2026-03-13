package com.elearning.admin.dao;

import com.elearning.admin.utils.DatabaseConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDAO {

    public List<ChatMessageDTO> getAll() {
        List<ChatMessageDTO> list = new ArrayList<>();
        String sql = "SELECT m.msg_id, m.message_text, m.created_at, " +
                "u.user_id, COALESCE(u.full_name, u.user_name, 'Unknown') as user_name, " +
                "COALESCE(u.avatar_url, '') as avatar_url " +
                "FROM chat_messages m " +
                "JOIN users u ON m.user_id = u.user_id " +
                "ORDER BY m.created_at DESC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ChatMessageDTO dto = new ChatMessageDTO();
                dto.msgId = rs.getInt("msg_id");
                dto.userId = rs.getInt("user_id");
                dto.userName = rs.getString("user_name");
                dto.avatarUrl = rs.getString("avatar_url");
                dto.message = rs.getString("message_text");
                dto.createdAt = rs.getTimestamp("created_at");
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ChatMessageDTO> search(String keyword) {
        List<ChatMessageDTO> list = new ArrayList<>();
        String sql = "SELECT m.msg_id, m.message_text, m.created_at, " +
                "u.user_id, COALESCE(u.full_name, u.user_name, 'Unknown') as user_name, " +
                "COALESCE(u.avatar_url, '') as avatar_url " +
                "FROM chat_messages m " +
                "JOIN users u ON m.user_id = u.user_id " +
                "WHERE m.message_text LIKE ? OR u.full_name LIKE ? OR u.user_name LIKE ? " +
                "ORDER BY m.created_at DESC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChatMessageDTO dto = new ChatMessageDTO();
                    dto.msgId = rs.getInt("msg_id");
                    dto.userId = rs.getInt("user_id");
                    dto.userName = rs.getString("user_name");
                    dto.avatarUrl = rs.getString("avatar_url");
                    dto.message = rs.getString("message_text");
                    dto.createdAt = rs.getTimestamp("created_at");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM chat_messages";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean delete(int msgId) {
        String sql = "DELETE FROM chat_messages WHERE msg_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, msgId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByUserId(int userId) {
        String sql = "DELETE FROM chat_messages WHERE user_id = ?";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class ChatMessageDTO {
        public int msgId;
        public int userId;
        public String userName;
        public String avatarUrl;
        public String message;
        public java.sql.Timestamp createdAt;
    }
}
