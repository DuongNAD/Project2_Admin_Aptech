package com.elearning.admin.dao;

import com.elearning.admin.models.Certificate;
import com.elearning.admin.utils.DatabaseConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CertificateDAO {
    public List<CertificateDTO> getAll() {
        List<CertificateDTO> list = new ArrayList<>();
        String sql = "SELECT cert.*, u.full_name as user_name, c.title as course_title " +
                "FROM certificates cert " +
                "JOIN users u ON cert.user_id = u.user_id " +
                "JOIN courses c ON cert.course_id = c.course_id " +
                "ORDER BY cert.issue_date DESC";
        try (Connection conn = DatabaseConnect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CertificateDTO dto = new CertificateDTO();
                Certificate cert = new Certificate();
                cert.setCertificateId(rs.getInt("certificate_id"));
                cert.setIssueDate(rs.getTimestamp("issue_date"));
                cert.setPdfUrl(rs.getString("pdf_url"));
                dto.certificate = cert;
                dto.userName = rs.getString("user_name");
                dto.courseTitle = rs.getString("course_title");
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class CertificateDTO {
        public Certificate certificate;
        public String userName;
        public String courseTitle;
    }
}
