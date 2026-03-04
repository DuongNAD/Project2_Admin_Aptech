package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import com.elearning.admin.dao.OrderDAO;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * Controller cho Orders & Revenue.
 * - Revenue stats + charts
 * - Orders list with filters
 * - Order detail panel
 */
public class OrdersController {

    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private DatePicker startDatePicker, endDatePicker;
    @FXML
    private Label countLabel;
    @FXML
    private TableView<OrderRow> ordersTable;
    @FXML
    private TableColumn<OrderRow, String> orderIdCol, userCol, amountCol, statusCol, createdCol;

    @FXML
    private LineChart<String, Number> revenueChart;
    @FXML
    private CategoryAxis revXAxis;
    @FXML
    private NumberAxis revYAxis;
    @FXML
    private BarChart<String, Number> ordersChart;
    @FXML
    private CategoryAxis ordXAxis;
    @FXML
    private NumberAxis ordYAxis;

    // Detail panel
    @FXML
    private Label lblOrderId, lblCustomer, lblTotal, lblStatus, lblDate, lblCoupon;
    @FXML
    private ListView<String> coursesList;

    private ObservableList<OrderRow> allOrders;
    private FilteredList<OrderRow> filteredOrders;

    @FXML
    public void initialize() {
        setupCharts();
        setupColumns();
        loadRealData();
        setupFilters();
        setupTableSelection();
        clearDetailPanel();
    }

    private void setupCharts() {
        // Revenue chart
        revenueChart.setAnimated(false);
        XYChart.Series<String, Number> revSeries = new XYChart.Series<>();
        revSeries.getData().add(new XYChart.Data<>("T1", 8_200_000));
        revSeries.getData().add(new XYChart.Data<>("T2", 9_100_000));
        revSeries.getData().add(new XYChart.Data<>("T3", 10_500_000));
        revSeries.getData().add(new XYChart.Data<>("T4", 12_450_000));
        revenueChart.getData().add(revSeries);

        // Orders chart
        ordersChart.setAnimated(false);
        ordXAxis.setAutoRanging(false);
        ordXAxis.setCategories(FXCollections.observableArrayList("T1", "T2", "T3", "T4"));
        ordYAxis.setAutoRanging(true);
        XYChart.Series<String, Number> ordSeries = new XYChart.Series<>();
        ordSeries.getData().add(new XYChart.Data<>("T1", 85));
        ordSeries.getData().add(new XYChart.Data<>("T2", 102));
        ordSeries.getData().add(new XYChart.Data<>("T3", 119));
        ordSeries.getData().add(new XYChart.Data<>("T4", 127));
        ordersChart.getData().add(ordSeries);
    }

    private void setupColumns() {
        orderIdCol.setCellValueFactory(c -> c.getValue().orderIdProperty());
        userCol.setCellValueFactory(c -> c.getValue().userProperty());
        amountCol.setCellValueFactory(c -> c.getValue().amountProperty());
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("orders-status-badge");
                    String styleClass = switch (item) {
                        case "Success" -> "orders-status-success";
                        case "Processing" -> "orders-status-processing";
                        case "Failed" -> "orders-status-failed";
                        case "Refunded" -> "orders-status-refunded";
                        default -> "orders-status-processing";
                    };
                    badge.getStyleClass().add(styleClass);
                    setGraphic(badge);
                }
            }
        });
        createdCol.setCellValueFactory(c -> c.getValue().createdProperty());
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadRealData() {
        OrderDAO dao = new OrderDAO();
        List<OrderDAO.OrderDTO> list = dao.getAll();
        ObservableList<OrderRow> rows = FXCollections.observableArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (OrderDAO.OrderDTO dto : list) {
            String orderIdStr = "#ORD-" + dto.order.getOrderId();
            String amountStr = String.format("%,.0f đ",
                    dto.order.getTotalAmount());
            String createdStr = dto.order.getCreatedAt() != null ? sdf.format(dto.order.getCreatedAt())  : "N/A";
            String status = dto.order.getStatus();
            if (status == null || status.isEmpty())
                status = "Processing";

            rows.add(new OrderRow(
                    orderIdStr,
                    dto.userName != null ? dto.userName : "N/A",
                    amountStr,
                    status,
                    createdStr,
                    "Khóa học kết hợp", // Demo courses
                    ""));
        }

        allOrders = rows;
        filteredOrders = new FilteredList<>(allOrders, p -> true);
        ordersTable.setItems(filteredOrders);
        updateCount();

        statusFilter.setItems(FXCollections.observableArrayList("Success", "Processing", "Failed", "Refunded"));
    }

    private void setupFilters() {
        statusFilter.valueProperty().addListener((o, a, b) -> applyFilters());
        startDatePicker.valueProperty().addListener((o, a, b) -> applyFilters());
        endDatePicker.valueProperty().addListener((o, a, b) -> applyFilters());
    }

    private void applyFilters() {
        filteredOrders.setPredicate(order -> {
            String status = statusFilter.getValue();
            if (status != null && !order.getStatus().equals(status)) {
                return false;
            }
            // Date filter (simplified - just for demo)
            return true;
        });
        updateCount();
    }

    private void updateCount() {
        countLabel.setText(filteredOrders.size() + " đơn hàng");
    }

    private void setupTableSelection() {
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showOrderDetail(newVal);
            } else {
                clearDetailPanel();
            }
        });
    }

    private void showOrderDetail(OrderRow order) {
        lblOrderId.setText(order.getOrderId());
        lblCustomer.setText(order.getUser());
        lblTotal.setText(order.getAmount());
        lblStatus.setText(order.getStatus());
        lblStatus.getStyleClass().removeAll("orders-status-success", "orders-status-processing", "orders-status-failed",
                "orders-status-refunded");
        String styleClass = switch (order.getStatus()) {
            case "Success" -> "orders-status-success";
            case "Processing" -> "orders-status-processing";
            case "Failed" -> "orders-status-failed";
            case "Refunded" -> "orders-status-refunded";
            default -> "orders-status-processing";
        };
        lblStatus.getStyleClass().add(styleClass);
        lblDate.setText(order.getCreated());
        lblCoupon.setText(
                order.getCoupon() == null || order.getCoupon().isEmpty() ? "Không có" : order.getCoupon() + " (-20%)");

        ObservableList<String> courses = FXCollections.observableArrayList(order.getCourses().split(", "));
        coursesList.setItems(courses);
    }

    private void clearDetailPanel() {
        lblOrderId.setText("Chọn một đơn hàng");
        lblCustomer.setText("-");
        lblTotal.setText("-");
        lblStatus.setText("-");
        lblDate.setText("-");
        lblCoupon.setText("-");
        coursesList.setItems(FXCollections.observableArrayList());
    }

    // Model
    public static class OrderRow {
        private final SimpleStringProperty orderId, user, amount, status, created, courses, coupon;

        public OrderRow(String orderId, String user, String amount, String status, String created, String courses,
                String coupon) {
            this.orderId = new SimpleStringProperty(orderId);
            this.user = new SimpleStringProperty(user);
            this.amount = new SimpleStringProperty(amount);
            this.status = new SimpleStringProperty(status);
            this.created = new SimpleStringProperty(created);
            this.courses = new SimpleStringProperty(courses);
            this.coupon = new SimpleStringProperty(coupon);
        }

        public String getOrderId() {
            return orderId.get();
        }

        public String getUser() {
            return user.get();
        }

        public String getAmount() {
            return amount.get();
        }

        public String getStatus() {
            return status.get();
        }

        public String getCreated() {
            return created.get();
        }

        public String getCourses() {
            return courses.get();
        }

        public String getCoupon() {
            return coupon.get();
        }

        public SimpleStringProperty orderIdProperty() {
            return orderId;
        }

        public SimpleStringProperty userProperty() {
            return user;
        }

        public SimpleStringProperty amountProperty() {
            return amount;
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }

        public SimpleStringProperty createdProperty() {
            return created;
        }
    }
}