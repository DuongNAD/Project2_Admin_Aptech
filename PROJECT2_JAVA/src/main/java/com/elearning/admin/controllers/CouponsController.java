package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import com.elearning.admin.dao.CouponDAO;
import com.elearning.admin.models.Coupon;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * Controller for Coupons management.
 * - Table list of coupons with stats
 * - Form to add/edit coupon
 */
public class CouponsController {

    @FXML
    private TableView<CouponRow> couponsTable;
    @FXML
    private TableColumn<CouponRow, String> codeCol, discountCol, activeCol, usageCol, expiryCol, actionsCol;
    @FXML
    private Label countLabel, formTitle;
    @FXML
    private TextField codeField, discountField, maxUsageField;
    @FXML
    private DatePicker expiryPicker;
    @FXML
    private CheckBox activeCheckbox;
    @FXML
    private Button btnSave, btnCancel;

    private ObservableList<CouponRow> coupons;
    private CouponRow selectedCoupon = null;

    @FXML
    public void initialize() {
        setupTable();
        loadRealData();
        setupTableSelection();
    }

    private void setupTable() {
        codeCol.setCellValueFactory(c -> c.getValue().codeProperty());
        discountCol.setCellValueFactory(c -> c.getValue().discountProperty());

        // Active status with badges
        activeCol.setCellValueFactory(c -> c.getValue().activeProperty());
        activeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("coupons-status-badge");
                    if ("Đang hoạt động".equals(item)) {
                        badge.getStyleClass().add("coupons-status-active");
                    } else {
                        badge.getStyleClass().add("coupons-status-inactive");
                    }
                    setGraphic(badge);
                }
            }
        });

        usageCol.setCellValueFactory(c -> c.getValue().usageProperty());
        expiryCol.setCellValueFactory(c -> c.getValue().expiryProperty());

        // Actions column
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("");
            private final Button btnDelete = new Button("");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                box.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().add("coupons-btn-edit");
                btnDelete.getStyleClass().add("coupons-btn-delete");

                btnEdit.setOnAction(e -> {
                    CouponRow row = getTableView().getItems().get(getIndex());
                    editCoupon(row);
                });

                btnDelete.setOnAction(e -> {
                    CouponRow row = getTableView().getItems().get(getIndex());
                    deleteCoupon(row);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        couponsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadRealData() {
        CouponDAO dao = new CouponDAO();
        List<Coupon> list = dao.getAll();
        ObservableList<CouponRow> rows = FXCollections.observableArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Coupon c : list) {
            String discountStr = String.format("%.0f%%", c.getDiscountPercent() * 100);
            String status = c.isActive() ? "Đang hoạt động" : "Hết hạn";
            String expiry = c.getExpirationDate() != null ? sdf.format(c.getExpirationDate())  : "Không xác định";

            rows.add(new CouponRow(
                    c.getCode(),
                    discountStr,
                    status,
                    "0/100",
                    expiry));
        }

        coupons = rows;
        couponsTable.setItems(coupons);
        updateCount();
    }

    private void setupTableSelection() {
        couponsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editCoupon(newVal);
            }
        });
    }

    private void editCoupon(CouponRow coupon) {
        selectedCoupon = coupon;
        formTitle.setText("Chỉnh sửa coupon");
        codeField.setText(coupon.getCode());
        discountField.setText(coupon.getDiscount().replace("%", ""));
        activeCheckbox.setSelected("Đang hoạt động".equals(coupon.getActive()));
        expiryPicker.setValue(LocalDate.now().plusMonths(1));
    }

    private void deleteCoupon(CouponRow coupon) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa coupon: " + coupon.getCode());
        alert.setContentText("Anh có chắc chắn muốn xóa mã giảm giá này không?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                coupons.remove(coupon);
                updateCount();
                handleCancel();
            }
        });
    }

    @FXML
    private void handleSave() {
        String code = codeField.getText().trim().toUpperCase();
        String discount = discountField.getText().trim();

        if (code.isEmpty() || discount.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin bắt buộc!");
            return;
        }

        try {
            int discountValue = Integer.parseInt(discount);
            if (discountValue < 0 || discountValue > 100) {
                showAlert("Lỗi", "Giảm giá phải từ 0-100%!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Giảm giá phải là số!");
            return;
        }

        String status = activeCheckbox.isSelected() ? "Đang hoạt động" : "Hết hạn";
        String expiry = expiryPicker.getValue() != null ? expiryPicker.getValue().toString() : "31/12/2026";

        if (selectedCoupon != null) {
            // Update existing
            selectedCoupon.setCode(code);
            selectedCoupon.setDiscount(discount + "%");
            selectedCoupon.setActive(status);
            selectedCoupon.setExpiry(expiry);
            couponsTable.refresh();
        } else {
            // Add new
            CouponRow newCoupon = new CouponRow(code, discount + "%", status, "0/100", expiry);
            coupons.add(newCoupon);
            updateCount();
        }

        handleCancel();
        showAlert("Thành công", "Coupon đã được lưu!");
    }

    @FXML
    private void handleCancel() {
        selectedCoupon = null;
        formTitle.setText("Tạo mã giảm giá mới");
        codeField.clear();
        discountField.clear();
        maxUsageField.clear();
        expiryPicker.setValue(null);
        activeCheckbox.setSelected(true);
        couponsTable.getSelectionModel().clearSelection();
    }

    private void updateCount() {
        countLabel.setText(coupons.size() + " coupons");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Model
    public static class CouponRow {
        private final SimpleStringProperty code, discount, active, usage, expiry;

        public CouponRow(String code, String discount, String active, String usage, String expiry) {
            this.code = new SimpleStringProperty(code);
            this.discount = new SimpleStringProperty(discount);
            this.active = new SimpleStringProperty(active);
            this.usage = new SimpleStringProperty(usage);
            this.expiry = new SimpleStringProperty(expiry);
        }

        public String getCode() {
            return code.get();
        }

        public void setCode(String value) {
            code.set(value);
        }

        public String getDiscount() {
            return discount.get();
        }

        public void setDiscount(String value) {
            discount.set(value);
        }

        public String getActive() {
            return active.get();
        }

        public void setActive(String value) {
            active.set(value);
        }

        public void setExpiry(String value) {
            expiry.set(value);
        }

        public SimpleStringProperty codeProperty() {
            return code;
        }

        public SimpleStringProperty discountProperty() {
            return discount;
        }

        public SimpleStringProperty activeProperty() {
            return active;
        }

        public SimpleStringProperty usageProperty() {
            return usage;
        }

        public SimpleStringProperty expiryProperty() {
            return expiry;
        }
    }
}