package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.elearning.admin.dao.CertificateDAO;
import java.util.List;
import java.text.SimpleDateFormat;

public class CertificatesController {

    @FXML
    private TableView<CertificateRow> certTable;

    @FXML
    private TableColumn<CertificateRow, String> userCol;

    @FXML
    private TableColumn<CertificateRow, String> courseCol;

    @FXML
    private TableColumn<CertificateRow, String> issueCol;

    @FXML
    private TableColumn<CertificateRow, String> linkCol;

    @FXML
    public void initialize() {
        userCol.setCellValueFactory(c -> c.getValue().userProperty());
        courseCol.setCellValueFactory(c -> c.getValue().courseProperty());
        issueCol.setCellValueFactory(c -> c.getValue().issueProperty());
        linkCol.setCellValueFactory(c -> c.getValue().linkProperty());

        loadRealData();
    }

    private void loadRealData() {
        CertificateDAO dao = new CertificateDAO();
        List<CertificateDAO.CertificateDTO> list = dao.getAll();
        ObservableList<CertificateRow> rows = FXCollections.observableArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (CertificateDAO.CertificateDTO dto : list) {
            String issueStr = dto.certificate.getIssueDate() != null ? sdf.format(dto.certificate.getIssueDate())
                     : "N/A";
            rows.add(new CertificateRow(
                    dto.userName != null ? dto.userName : "N/A",
                    dto.courseTitle != null ? dto.courseTitle : "N/A",
                    issueStr,
                    dto.certificate.getPdfUrl() != null ? dto.certificate.getPdfUrl() : "N/A"));
        }

        certTable.setItems(rows);
    }

    public static class CertificateRow {
        private final SimpleStringProperty user;
        private final SimpleStringProperty course;
        private final SimpleStringProperty issue;
        private final SimpleStringProperty link;

        public CertificateRow(String user, String course, String issue, String link) {
            this.user = new SimpleStringProperty(user);
            this.course = new SimpleStringProperty(course);
            this.issue = new SimpleStringProperty(issue);
            this.link = new SimpleStringProperty(link);
        }

        public SimpleStringProperty userProperty() {
            return user;
        }

        public SimpleStringProperty courseProperty() {
            return course;
        }

        public SimpleStringProperty issueProperty() {
            return issue;
        }

        public SimpleStringProperty linkProperty() {
            return link;
        }
    }
}
