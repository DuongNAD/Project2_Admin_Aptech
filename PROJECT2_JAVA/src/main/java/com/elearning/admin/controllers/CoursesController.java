package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.elearning.admin.dao.CourseDAO;
import com.elearning.admin.dao.CategoryDAO;
import java.util.List;
import java.text.SimpleDateFormat;

import java.util.function.Predicate;

/**
 * Controller cho màn hình Quản lý Courses.
 * - Danh sách với filter, TableView, actions
 * - Chuyển sang Course Detail khi bấm Xem chi tiết
 */
public class CoursesController {

    @FXML
    private VBox listView;
    @FXML
    private StackPane detailContainer;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private TableView<CourseRow> coursesTable;
    @FXML
    private TableColumn<CourseRow, String> titleCol;
    @FXML
    private TableColumn<CourseRow, String> categoryCol;
    @FXML
    private TableColumn<CourseRow, String> instructorCol;
    @FXML
    private TableColumn<CourseRow, String> priceCol;
    @FXML
    private TableColumn<CourseRow, String> statusCol;
    @FXML
    private TableColumn<CourseRow, String> createdAtCol;
    @FXML
    private Label countLabel;

    private ObservableList<CourseRow> allCourses;
    private FilteredList<CourseRow> filteredCourses;

    @FXML
    public void initialize() {
        setupColumns();
        loadRealData();
        setupFilters();
        setupTableSelection();
    }

    private void setupColumns() {
        titleCol.setCellValueFactory(c -> c.getValue().titleProperty());
        categoryCol.setCellValueFactory(c -> c.getValue().categoryProperty());
        instructorCol.setCellValueFactory(c -> c.getValue().instructorProperty());
        priceCol.setCellValueFactory(c -> c.getValue().priceProperty());
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());
        createdAtCol.setCellValueFactory(c -> c.getValue().createdAtProperty());
        coursesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadRealData() {
        CourseDAO courseDAO = new CourseDAO();
        CategoryDAO catDAO = new CategoryDAO();

        List<CourseDAO.CourseDTO> courses = courseDAO.getAllCoursesWithDetails();
        ObservableList<CourseRow> rows = FXCollections.observableArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (CourseDAO.CourseDTO c : courses) {
            String createdAt = c.course.getCreatedAt() != null ? sdf.format(c.course.getCreatedAt()) : "N/A";
            String priceStr = c.course.getPrice() != null ? String.format("%,.0f đ", c.course.getPrice().doubleValue())
                    : "0 đ";
            String status = c.course.getStatus();
            // Normalize DB status to display labels
            if (status == null)
                status = "Draft";
            else if (status.equalsIgnoreCase("active") || status.equalsIgnoreCase("published"))
                status = "Published";
            else if (status.equalsIgnoreCase("draft"))
                status = "Draft";
            else if (status.equalsIgnoreCase("archived"))
                status = "Archived";
            else
                status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();

            rows.add(new CourseRow(
                    c.course.getCourseId(),
                    c.course.getTitle(),
                    c.categoryName,
                    c.instructorName,
                    priceStr,
                    status,
                    createdAt));
        }

        allCourses = rows;
        filteredCourses = new FilteredList<>(allCourses, p -> true);
        coursesTable.setItems(filteredCourses);

        List<com.elearning.admin.models.Category> cats = catDAO.getAll();
        ObservableList<String> catNames = FXCollections.observableArrayList();
        catNames.add("Tất cả");
        for (com.elearning.admin.models.Category c : cats) {
            catNames.add(c.getName());
        }
        categoryFilter.setItems(catNames);
        categoryFilter.setValue("Tất cả");

        statusFilter.setItems(FXCollections.observableArrayList("Tất cả", "Published", "Draft", "Archived"));
        statusFilter.setValue("Tất cả");
    }

    private void setupFilters() {
        Predicate<CourseRow> filter = p -> {
            String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
            String cat = categoryFilter.getValue();
            String status = statusFilter.getValue();
            if (search != null && !search.isBlank() && !p.getTitle().toLowerCase().contains(search))
                return false;
            if (cat != null && !cat.equals("Tất cả") && !cat.equals(p.getCategory()))
                return false;
            if (status != null && !status.equals("Tất cả") && !status.equals(p.getStatus()))
                return false;
            return true;
        };
        Runnable updateCount = () -> {
            if (countLabel != null) {
                countLabel.setText(filteredCourses.size() + " khóa học");
            }
        };
        searchField.textProperty().addListener((o, a, b) -> {
            filteredCourses.setPredicate(filter);
            updateCount.run();
        });
        categoryFilter.valueProperty().addListener((o, a, b) -> {
            filteredCourses.setPredicate(filter);
            updateCount.run();
        });
        statusFilter.valueProperty().addListener((o, a, b) -> {
            filteredCourses.setPredicate(filter);
            updateCount.run();
        });
        updateCount.run();
    }

    private void setupTableSelection() {
        coursesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    private void onClearFilterClicked() {
        searchField.clear();
        categoryFilter.setValue("Tất cả");
        statusFilter.setValue("Tất cả");
    }

    /**
     * Thêm khóa học mới (demo, dữ liệu in-memory).
     * Mặc định tạo 1 khóa học Draft rồi mở màn chi tiết.
     */
    @FXML
    private void onAddCourseClicked() {
        // tạo course mới với dữ liệu mặc định (Draft)
        CourseRow newCourse = new CourseRow(
                0, // ID mock cho khóa học mới
                "Khóa học mới",
                "Programming",
                "Admin",
                "0 đ",
                "Draft",
                "Hôm nay");
        allCourses.add(0, newCourse);
        filteredCourses.setPredicate(filteredCourses.getPredicate()); // refresh filter
        coursesTable.getSelectionModel().select(newCourse);
        showCourseDetail(newCourse, true, false);
    }

    @FXML
    private void onViewDetailClicked() {
        CourseRow selected = coursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chọn một khóa học để xem chi tiết.");
            return;
        }
        showCourseDetail(selected, false, true);
    }

    @FXML
    private void onEditClicked() {
        CourseRow selected = coursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chọn một khóa học để chỉnh sửa.");
            return;
        }
        showCourseDetail(selected, false, false);
    }

    @FXML
    private void onArchiveClicked() {
        CourseRow selected = coursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Chọn một khóa học để xóa.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xóa khóa học");
        alert.setHeaderText("Xác nhận xóa khóa học");
        alert.setContentText("Bạn có chắc chắn muốn xóa khóa học '" + selected.getTitle()
                + "' không? Hành động này không thể hoàn tác.");

        java.util.Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CourseDAO courseDAO = new CourseDAO();
            if (courseDAO.delete(selected.getCourseId())) {
                allCourses.remove(selected);
                showAlert("Xóa thành công.");
            } else {
                showAlert("Không thể xóa khóa học. Có thể khóa học đang được sử dụng ở nơi khác.");
            }
        }
    }

    private void showCourseDetail(CourseRow course) {
        showCourseDetail(course, false, false);
    }

    private void showCourseDetail(CourseRow course, boolean isNew, boolean isReadOnly) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/course_detail_view.fxml"));
            loader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
            Parent detailView = loader.load();
            CourseDetailController ctrl = loader.getController();

            ctrl.setNewCourse(isNew);
            ctrl.setReadOnly(isReadOnly);
            ctrl.setCourse(course);
            ctrl.setOnBack(this::showList);
            detailContainer.getChildren().clear();
            detailContainer.getChildren().add(detailView);
            listView.setVisible(false);
            listView.setManaged(false);
            detailContainer.setVisible(true);
            detailContainer.setManaged(true);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể tải màn hình chi tiết.");
        }
    }

    private void showList() {
        detailContainer.getChildren().clear();
        detailContainer.setVisible(false);
        detailContainer.setManaged(false);
        listView.setVisible(true);
        listView.setManaged(true);
        loadRealData();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static class CourseRow {
        private int courseId;
        private final SimpleStringProperty title, category, instructor, price, status, createdAt;

        public CourseRow(int courseId, String title, String category, String instructor, String price, String status,
                String createdAt) {
            this.courseId = courseId;
            this.title = new SimpleStringProperty(title);
            this.category = new SimpleStringProperty(category);
            this.instructor = new SimpleStringProperty(instructor);
            this.price = new SimpleStringProperty(price);
            this.status = new SimpleStringProperty(status);
            this.createdAt = new SimpleStringProperty(createdAt);
        }

        public int getCourseId() {
            return courseId;
        }

        public String getTitle() {
            return title.get();
        }

        public String getCategory() {
            return category.get();
        }

        public String getStatus() {
            return status.get();
        }

        public SimpleStringProperty titleProperty() {
            return title;
        }

        public SimpleStringProperty categoryProperty() {
            return category;
        }

        public SimpleStringProperty instructorProperty() {
            return instructor;
        }

        public SimpleStringProperty priceProperty() {
            return price;
        }

        public SimpleStringProperty statusProperty() {
            return status;
        }

        public SimpleStringProperty createdAtProperty() {
            return createdAt;
        }
    }
}