package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.elearning.admin.dao.DashboardDAO;
import java.util.List;

public class DashboardController {

    @FXML
    private Label revenueLabel;
    @FXML
    private Label coursesCountLabel;
    @FXML
    private Label studentsCountLabel;

    @FXML
    private LineChart<String, Number> revenueChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private BarChart<String, Number> enrollmentChart;

    @FXML
    private CategoryAxis enrollXAxis;

    @FXML
    private NumberAxis enrollYAxis;

    @FXML
    private TableView<EnrollmentRow> ordersTable;

    @FXML
    private TableColumn<EnrollmentRow, String> enrollmentDateCol;

    @FXML
    private TableColumn<EnrollmentRow, String> studentCol;

    @FXML
    private TableColumn<EnrollmentRow, String> courseCol;

    @FXML
    private TableColumn<EnrollmentRow, String> progressCol;

    @FXML
    private TableColumn<EnrollmentRow, String> statusCol;

    @FXML
    public void initialize() {
        setupOrdersTable();
        loadRealData();
    }

    private void loadRealData() {
        DashboardDAO dao = new DashboardDAO();
        DashboardDAO.DashboardMetrics metrics = dao.getMetrics();

        if (revenueLabel != null)
            revenueLabel.setText(String.format("$%,.2f", metrics.totalRevenue));
        if (coursesCountLabel != null)
            coursesCountLabel.setText(String.valueOf(metrics.totalCourses));
        if (studentsCountLabel != null)
            studentsCountLabel.setText(String.format("%,d", metrics.totalStudents));

        List<DashboardDAO.RecentEnrollmentDTO> recent = dao.getRecentEnrollments(10);
        ObservableList<EnrollmentRow> rows = FXCollections.observableArrayList();
        for (DashboardDAO.RecentEnrollmentDTO r : recent) {
            rows.add(new EnrollmentRow(r.enrollmentDate, r.studentName, r.courseTitle, r.progress, r.status));
        }
        ordersTable.setItems(rows);

        int currentYear = java.time.Year.now().getValue();
        double[] rev = dao.getRevenueByQuarter(currentYear);
        int[] stu = dao.getNewStudentsByQuarter(currentYear);
        setupChart(rev);
        setupEnrollmentChart(stu);
    }

    private void setupChart(double[] rev) {
        xAxis.setLabel("Quý");
        yAxis.setLabel("Doanh thu ($)");

        revenueChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Q1", rev[0]));
        series.getData().add(new XYChart.Data<>("Q2", rev[1]));
        series.getData().add(new XYChart.Data<>("Q3", rev[2]));
        series.getData().add(new XYChart.Data<>("Q4", rev[3]));

        revenueChart.getData().add(series);
    }

    private void setupEnrollmentChart(int[] stu) {
        enrollXAxis.setLabel("Quý");
        enrollYAxis.setLabel("Học viên mới");

        enrollmentChart.getData().clear();
        enrollmentChart.setAnimated(false);

        enrollXAxis.setAutoRanging(false);
        enrollXAxis.setCategories(FXCollections.observableArrayList("Q1", "Q2", "Q3", "Q4"));
        enrollYAxis.setAutoRanging(true);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Học viên mới");
        series.getData().add(new XYChart.Data<>("Q1", stu[0]));
        series.getData().add(new XYChart.Data<>("Q2", stu[1]));
        series.getData().add(new XYChart.Data<>("Q3", stu[2]));
        series.getData().add(new XYChart.Data<>("Q4", stu[3]));

        enrollmentChart.getData().add(series);
    }

    private void setupOrdersTable() {
        enrollmentDateCol.setCellValueFactory(cell -> cell.getValue().enrollmentDateProperty());
        studentCol.setCellValueFactory(cell -> cell.getValue().studentProperty());
        courseCol.setCellValueFactory(cell -> cell.getValue().courseProperty());
        progressCol.setCellValueFactory(cell -> cell.getValue().progressProperty());
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public static class EnrollmentRow {
        private final SimpleStringProperty enrollmentDate;
        private final SimpleStringProperty student;
        private final SimpleStringProperty course;
        private final SimpleStringProperty progress;
        private final SimpleStringProperty status;

        public EnrollmentRow(String enrollmentDate, String student, String course, String progress, String status) {
            this.enrollmentDate = new SimpleStringProperty(enrollmentDate);
            this.student = new SimpleStringProperty(student);
            this.course = new SimpleStringProperty(course);
            this.progress = new SimpleStringProperty(progress);
            this.status = new SimpleStringProperty(status);
        }

        public SimpleStringProperty enrollmentDateProperty() {
            return enrollmentDate;
        }

        public SimpleStringProperty studentProperty() {
            return student;
        }

        public SimpleStringProperty courseProperty() {
            return course;
        }

        public SimpleStringProperty progressProperty() {
            return progress;
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }
    }
}
