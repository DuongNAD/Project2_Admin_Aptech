package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import com.elearning.admin.dao.UserDAO;
import java.util.List;
import java.text.SimpleDateFormat;

public class UsersController {

    @FXML
    private TextField searchField;
    @FXML
    private Button tabAll, tabStudents, tabInstructors, tabAdmins;
    @FXML
    private Label countLabel;
    @FXML
    private TableView<UserRow> usersTable;
    @FXML
    private TableColumn<UserRow, String> avatarCol, nameCol, emailCol, roleCol, statusCol, createdCol;

    // Detail panel
    @FXML
    private Label lblAvatarLetter, lblDetailName, lblDetailEmail, lblDetailRole, lblDetailStatus;
    @FXML
    private Label lblCoursesCount, lblEnrollDate, lblCoursesTitle;
    @FXML
    private ListView<String> coursesList;
    @FXML
    private Button btnBan, btnDelete;

    private ObservableList<UserRow> allUsers;
    private FilteredList<UserRow> filteredUsers;
    private String currentRoleFilter = null;

    @FXML
    public void initialize() {
        setupColumns();
        loadRealData();
        setupFilters();
        setupTableSelection();
        clearDetailPanel();
    }

    private void setupColumns() {
        avatarCol.setCellValueFactory(c -> c.getValue().avatarProperty());
        avatarCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    StackPane avatar = new StackPane();
                    avatar.getStyleClass().add("users-avatar-small");
                    Label letter = new Label(item);
                    letter.getStyleClass().add("users-avatar-letter");
                    avatar.getChildren().add(letter);
                    setGraphic(avatar);
                }
            }
        });
        nameCol.setCellValueFactory(c -> c.getValue().nameProperty());
        emailCol.setCellValueFactory(c -> c.getValue().emailProperty());
        roleCol.setCellValueFactory(c -> c.getValue().roleProperty());
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("users-role-badge");
                    badge.getStyleClass().add("users-role-" + item.toLowerCase());
                    setGraphic(badge);
                }
            }
        });
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("users-status-badge");
                    badge.getStyleClass().add(item.equals("Active") ? "users-status-active" : "users-status-banned");
                    setGraphic(badge);
                }
            }
        });
        createdCol.setCellValueFactory(c -> c.getValue().createdProperty());
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadRealData() {
        UserDAO dao = new UserDAO();
        List<UserDAO.UserWithCount> usersFromDb = dao.getAllUsersWithCourseCount();

        ObservableList<UserRow> rows = FXCollections.observableArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (UserDAO.UserWithCount uw : usersFromDb) {
            String role = uw.user.getRole();
            if (role != null && role.length() > 0)
                role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();

            String createdAt = uw.user.getCreatedAt() != null ? sdf.format(uw.user.getCreatedAt()) : "N/A";

            rows.add(new UserRow(
                    uw.user.getUserId(),
                    uw.user.getFullName(),
                    uw.user.getEmail(),
                    role,
                    uw.status,
                    createdAt,
                    uw.count));
        }

        allUsers = rows;
        filteredUsers = new FilteredList<>(allUsers, p -> true);
        usersTable.setItems(filteredUsers);
        updateCount();
    }

    private void setupFilters() {
        searchField.textProperty().addListener((o, a, b) -> applyFilters());
    }

    private void applyFilters() {
        filteredUsers.setPredicate(user -> {
            String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
            if (!search.isBlank() && !user.getName().toLowerCase().contains(search)
                    && !user.getEmail().toLowerCase().contains(search)) {
                return false;
            }
            if (currentRoleFilter != null && !user.getRole().equals(currentRoleFilter)) {
                return false;
            }
            return true;
        });
        updateCount();
    }

    private void updateCount() {
        countLabel.setText(filteredUsers.size() + " người dùng");
    }

    private void setupTableSelection() {
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showUserDetail(newVal);
            } else {
                clearDetailPanel();
            }
        });
    }

    private void showUserDetail(UserRow user) {
        lblAvatarLetter.setText(
                user.getName() != null && !user.getName().isEmpty() ? user.getName().substring(0, 1).toUpperCase()
                        : "");
        lblDetailName.setText(user.getName());
        lblDetailEmail.setText(user.getEmail());
        lblDetailRole.setText(user.getRole());
        lblDetailRole.getStyleClass().removeAll("users-role-student", "users-role-instructor", "users-role-admin");
        lblDetailRole.getStyleClass().add("users-role-" + user.getRole().toLowerCase());

        lblDetailStatus.setText(user.getStatus());
        lblDetailStatus.getStyleClass().removeAll("users-status-active", "users-status-banned");
        lblDetailStatus.getStyleClass()
                .add(user.getStatus().equals("Active") ? "users-status-active" : "users-status-banned");

        lblCoursesCount.setText(String.valueOf(user.getCoursesCount()));
        lblEnrollDate.setText(user.getCreated());

        // Update courses list title based on role
        if (user.getRole().equals("Instructor")) {
            lblCoursesTitle.setText("Khóa học đã tạo");
        } else {
            lblCoursesTitle.setText("Khóa học đã đăng ký");
        }

        // Fake courses
        ObservableList<String> courses = FXCollections.observableArrayList();
        if (user.getCoursesCount() > 0) {
            courses.add("Java Cơ bản");
            if (user.getCoursesCount() > 1)
                courses.add("Spring Boot API");
            if (user.getCoursesCount() > 2)
                courses.add("React Fundamentals");
            if (user.getCoursesCount() > 3)
                courses.add("Python Data Science");
        }
        coursesList.setItems(courses);

        // Update ban button text
        if (user.getStatus().equals("Active")) {
            btnBan.setText("🚫 Cấm người dùng");
            btnBan.getStyleClass().removeAll("users-btn-unban");
            btnBan.getStyleClass().add("users-btn-ban");
        } else {
            btnBan.setText("✓ Gỡ cấm");
            btnBan.getStyleClass().removeAll("users-btn-ban");
            btnBan.getStyleClass().add("users-btn-unban");
        }
    }

    private void clearDetailPanel() {
        lblAvatarLetter.setText("");
        lblDetailName.setText("Chọn một người dùng");
        lblDetailEmail.setText("");
        lblDetailRole.setText("");
        lblDetailStatus.setText("");
        lblCoursesCount.setText("0");
        lblEnrollDate.setText("-");
        coursesList.setItems(FXCollections.observableArrayList());
    }

    // Tab handlers
    @FXML
    private void onTabAllClicked() {
        setActiveTab(tabAll);
        currentRoleFilter = null;
        applyFilters();
    }

    @FXML
    private void onTabStudentsClicked() {
        setActiveTab(tabStudents);
        currentRoleFilter = "Student";
        applyFilters();
    }

    @FXML
    private void onTabInstructorsClicked() {
        setActiveTab(tabInstructors);
        currentRoleFilter = "Instructor";
        applyFilters();
    }

    @FXML
    private void onTabAdminsClicked() {
        setActiveTab(tabAdmins);
        currentRoleFilter = "Admin";
        applyFilters();
    }

    private void setActiveTab(Button active) {
        Button[] tabs = { tabAll, tabStudents, tabInstructors, tabAdmins };
        for (Button t : tabs) {
            t.getStyleClass().remove("users-tab-active");
        }
        active.getStyleClass().add("users-tab-active");
    }

    // Action handlers
    @FXML
    private void onBanClicked() {
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chọn một người dùng để thực hiện.");
            return;
        }
        String action = selected.getStatus().equals("Active") ? "cấm" : "gỡ cấm";
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Bạn có chắc muốn " + action + " người dùng này");
        confirm.setContentText(selected.getName() + " (" + selected.getEmail() + ")");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Toggle status
                String newStatus = selected.getStatus().equals("Active") ? "Banned" : "Active";

                UserDAO dao = new UserDAO();
                boolean success = dao.updateUserStatus(selected.getUserId(), newStatus);
                if (success) {
                    selected.setStatus(newStatus);
                    usersTable.refresh();
                    showUserDetail(selected);
                    showAlert("Đã " + action + " người dùng: " + selected.getName());
                } else {
                    showAlert("Có lỗi xảy ra khi " + action + " người dùng này.");
                }
            }
        });
    }

    @FXML
    private void onDeleteClicked() {
        UserRow selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chọn một người dùng để xóa.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa vĩnh viễn người dùng này?");
        confirm.setContentText(
                selected.getName() + " (" + selected.getEmail() + ")\n\nHành động này không thể hoàn tác!");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                UserDAO dao = new UserDAO();
                boolean success = dao.deleteUser(selected.getUserId());
                if (success) {
                    allUsers.remove(selected);
                    clearDetailPanel();
                    updateCount();
                    showAlert("Đã xóa người dùng: " + selected.getName());
                } else {
                    showAlert(
                            "Không thể xóa người dùng này! Có thể người dùng đã có liên kết khóa học hoặc dữ liệu quan trọng.");
                }
            }
        });
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Model class
    public static class UserRow {
        private final int userId;
        private final SimpleStringProperty avatar, name, email, role, status, created;
        private int coursesCount;

        public UserRow(int userId, String name, String email, String role, String status, String created,
                int coursesCount) {
            this.userId = userId;
            this.avatar = new SimpleStringProperty(
                    name != null && !name.isEmpty() ? name.substring(0, 1).toUpperCase() : "");
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
            this.role = new SimpleStringProperty(role);
            this.status = new SimpleStringProperty(status);
            this.created = new SimpleStringProperty(created);
            this.coursesCount = coursesCount;
        }

        public int getUserId() {
            return userId;
        }

        public String getName() {
            return name.get();
        }

        public String getEmail() {
            return email.get();
        }

        public String getRole() {
            return role.get();
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String s) {
            status.set(s);
        }

        public String getCreated() {
            return created.get();
        }

        public int getCoursesCount() {
            return coursesCount;
        }

        public SimpleStringProperty avatarProperty() {
            return avatar;
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public SimpleStringProperty emailProperty() {
            return email;
        }

        public SimpleStringProperty roleProperty() {
            return role;
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }

        public SimpleStringProperty createdProperty() {
            return created;
        }
    }
}