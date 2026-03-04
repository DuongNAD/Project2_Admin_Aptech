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
    private TableView<OrderRow> ordersTable;

    @FXML
    private TableColumn<OrderRow, String> orderIdCol;

    @FXML
    private TableColumn<OrderRow, String> studentCol;

    @FXML
    private TableColumn<OrderRow, String> courseCol;

    @FXML
    private TableColumn<OrderRow, String> totalCol;

    @FXML
    private TableColumn<OrderRow, String> statusCol;

    @FXML
    public void initialize() {
        setupChart();
        setupEnrollmentChart();
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

        List<DashboardDAO.RecentOrderDTO> recent = dao.getRecentOrders(10);
        ObservableList<OrderRow> rows = FXCollections.observableArrayList();
        for (DashboardDAO.RecentOrderDTO r : recent) {
            rows.add(new OrderRow(r.orderId, r.studentName, r.courseTitle, r.total, r.status));
        }
        ordersTable.setItems(rows);
    }

    private void setupChart() {
        xAxis.setLabel("Quý");
        yAxis.setLabel("Doanh thu ($)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Q1", 120_000));
        series.getData().add(new XYChart.Data<>("Q2", 180_000));
        series.getData().add(new XYChart.Data<>("Q3", 230_000));
        series.getData().add(new XYChart.Data<>("Q4", 348_261));

        revenueChart.getData().add(series);
    }

    private void setupEnrollmentChart() {
        enrollXAxis.setLabel("Quý");
        enrollYAxis.setLabel("Học viên mới");

        enrollmentChart.getData().clear();
        enrollmentChart.setAnimated(false);

        enrollXAxis.setAutoRanging(false);
        enrollXAxis.setCategories(FXCollections.observableArrayList("Q1", "Q2", "Q3", "Q4"));
        enrollYAxis.setAutoRanging(true);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Học viên mới");
        series.getData().add(new XYChart.Data<>("Q1", 320));
        series.getData().add(new XYChart.Data<>("Q2", 480));
        series.getData().add(new XYChart.Data<>("Q3", 650));
        series.getData().add(new XYChart.Data<>("Q4", 820));

        enrollmentChart.getData().add(series);
    }

    private void setupOrdersTable() {
        orderIdCol.setCellValueFactory(cell -> cell.getValue().orderIdProperty());
        studentCol.setCellValueFactory(cell -> cell.getValue().studentProperty());
        courseCol.setCellValueFactory(cell -> cell.getValue().courseProperty());
        totalCol.setCellValueFactory(cell -> cell.getValue().totalProperty());
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public static class OrderRow {
        private final SimpleStringProperty orderId;
        private final SimpleStringProperty student;
        private final SimpleStringProperty course;
        private final SimpleStringProperty total;
        private final SimpleStringProperty status;

        public OrderRow(String orderId, String student, String course, String total, String status) {
            this.orderId = new SimpleStringProperty(orderId);
            this.student = new SimpleStringProperty(student);
            this.course = new SimpleStringProperty(course);
            this.total = new SimpleStringProperty(total);
            this.status = new SimpleStringProperty(status);
        }

        public SimpleStringProperty orderIdProperty() {
            return orderId;
        }

        public SimpleStringProperty studentProperty() {
            return student;
        }

        public SimpleStringProperty courseProperty() {
            return course;
        }

        public SimpleStringProperty totalProperty() {
            return total;
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }
    }
}
