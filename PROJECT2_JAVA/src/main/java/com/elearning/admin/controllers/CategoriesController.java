package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import com.elearning.admin.dao.CategoryDAO;
import com.elearning.admin.models.Category;
import java.util.List;

/**
 * Controller for Categories management.
 * - Table list of categories
 * - Form to add/edit category
 */
public class CategoriesController {

    @FXML
    private TableView<CategoryRow> categoriesTable;
    @FXML
    private TableColumn<CategoryRow, String> nameCol, descriptionCol, coursesCountCol, actionsCol;
    @FXML
    private Label countLabel, formTitle;
    @FXML
    private TextField nameField, slugField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button btnSave, btnCancel;

    private ObservableList<CategoryRow> categories;
    private CategoryRow selectedCategory = null;

    @FXML
    public void initialize() {
        setupTable();
        loadRealData();
        setupTableSelection();
    }

    private void setupTable() {
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());
        descriptionCol.setCellValueFactory(c -> c.getValue().descriptionProperty());
        coursesCountCol.setCellValueFactory(c -> c.getValue().coursesCountProperty());

        // Actions column with Edit/Delete buttons
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏ Sửa");
            private final Button btnDelete = new Button("🗑 Xóa");
            private final HBox box = new HBox(6, btnEdit, btnDelete);

            {
                box.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().add("categories-btn-edit");
                btnDelete.getStyleClass().add("categories-btn-delete");

                btnEdit.setOnAction(e -> {
                    CategoryRow row = getTableView().getItems().get(getIndex());
                    editCategory(row);
                });

                btnDelete.setOnAction(e -> {
                    CategoryRow row = getTableView().getItems().get(getIndex());
                    deleteCategory(row);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        categoriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadRealData() {
        CategoryDAO dao = new CategoryDAO();
        List<Category> cats = dao.getAll();
        ObservableList<CategoryRow> rows = FXCollections.observableArrayList();
        for (Category c : cats) {
            String desc = c.getDescription();
            if (desc == null)
                desc = "";
            rows.add(new CategoryRow(c.getCategoryId(), c.getName(), desc, "0")); // fake course count
        }
        categories = rows;
        categoriesTable.setItems(categories);
        updateCount();
    }

    private void setupTableSelection() {
        categoriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editCategory(newVal);
            }
        });
    }

    private void editCategory(CategoryRow category) {
        selectedCategory = category;
        formTitle.setText("Chỉnh sửa danh mục");
        nameField.setText(category.getName());
        descriptionField.setText(category.getDescription());
        slugField.setText(category.getName().toLowerCase().replace(" ", "-"));
    }

    private void deleteCategory(CategoryRow category) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa danh mục: " + category.getName());
        alert.setContentText("Bạn có chắc chắn muốn xóa danh mục này? Hành động này không thể hoàn tác.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                CategoryDAO dao = new CategoryDAO();
                boolean success = dao.delete(category.getId());
                if (success) {
                    loadRealData();
                    handleCancel();
                    showAlert("Thành công", "Đã xóa danh mục!");
                } else {
                    showAlert("Lỗi", "Không thể xóa danh mục này (có thể do ràng buộc khóa ngoài).");
                }
            }
        });
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String desc = descriptionField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Lỗi", "Tên danh mục không được để trống!");
            return;
        }

        CategoryDAO dao = new CategoryDAO();
        boolean success;

        if (selectedCategory != null) {
            Category cat = new Category();
            cat.setCategoryId(selectedCategory.getId());
            cat.setName(name);
            cat.setDescription(desc);
            success = dao.update(cat);
        } else {
            Category newCat = new Category();
            newCat.setName(name);
            newCat.setDescription(desc);
            success = dao.insert(newCat);
        }

        if (success) {
            loadRealData();
            handleCancel();
            showAlert("Thành công", "Danh mục đã được lưu!");
        } else {
            showAlert("Lỗi", "Có lỗi xảy ra khi lưu danh mục.");
        }
    }

    @FXML
    private void handleCancel() {
        selectedCategory = null;
        formTitle.setText("Thêm danh mục mới");
        nameField.clear();
        descriptionField.clear();
        slugField.clear();
        categoriesTable.getSelectionModel().clearSelection();
    }

    private void updateCount() {
        countLabel.setText(categories.size() + " danh mục");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Model
    public static class CategoryRow {
        private final int id;
        private final SimpleStringProperty name, description, coursesCount;

        public CategoryRow(int id, String name, String description, String coursesCount) {
            this.id = id;
            this.name = new SimpleStringProperty(name);
            this.description = new SimpleStringProperty(description);
            this.coursesCount = new SimpleStringProperty(coursesCount);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name.get();
        }

        public void setName(String value) {
            name.set(value);
        }

        public String getDescription() {
            return description.get();
        }

        public void setDescription(String value) {
            description.set(value);
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public SimpleStringProperty coursesCountProperty() {
            return coursesCount;
        }
    }
}