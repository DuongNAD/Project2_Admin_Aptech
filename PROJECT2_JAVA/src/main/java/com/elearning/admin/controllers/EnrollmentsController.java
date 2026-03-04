package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.elearning.admin.dao.EnrollmentDAO;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * Controller for Enrollments & Progress.
 * - Table of enrollments with filters
 * - Detail panel showing lesson progress
 */
public class EnrollmentsController {

    @FXML
    private ComboBox<String> courseFilter, statusFilter, progressFilter;
    @FXML
    private TextField searchField;
    @FXML
    private Label countLabel;
    @FXML
    private TableView<EnrollmentRow> enrollmentsTable;
    @FXML
    private TableColumn<EnrollmentRow, String> userCol, courseCol, progressCol, statusCol, enrolledCol;

    // Detail panel
    @FXML
    private Label lblStudent, lblCourse, lblStatus, lblProgress, lblEnrolled, lblLastActivity, lblLessonCount;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private VBox lessonsContainer;

    private ObservableList<EnrollmentRow> allEnrollments;
    private FilteredList<EnrollmentRow> filteredEnrollments;

    @FXML
    public void initialize() {
        setupTable();
        loadRealData();
        setupFilters();
        setupTableSelection();
        clearDetailPanel();
    }

    private void setupTable() {
        userCol.setCellValueFactory(c -> c.getValue().userProperty());
        courseCol.setCellValueFactory(c -> c.getValue().courseProperty());

        // Progress column with progress bar
        progressCol.setCellValueFactory(c -> c.getValue().progressProperty());
        progressCol.setCellFactory(col -> new TableCell<>() {
            private final ProgressBar bar = new ProgressBar();
            private final Label label = new Label();
            private final VBox box = new VBox(4, bar, label);

            {
                bar.setPrefWidth(100);
                bar.setPrefHeight(6);
                bar.getStyleClass().add("enrollments-progress-bar-small");
                label.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 10px;");
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    int progress = Integer.parseInt(item.replace("%", ""));
                    bar.setProgress(progress / 100.0);
                    label.setText(item);
                    setGraphic(box);
                }
            }
        });

        // Status column with badges
        statusCol.setCellValueFactory(c -> c.getValue().statusProperty());
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("enrollments-status-badge");
                    String styleClass = switch (item) {
                        case "Đang học" -> "enrollments-status-active";
                        case "Hoàn thành" -> "enrollments-status-completed";
                        case "Tạm dừng" -> "enrollments-status-paused";
                        default -> "enrollments-status-active";
                    };
                    badge.getStyleClass().add(styleClass);
                    setGraphic(badge);
                }
            }
        });

        enrolledCol.setCellValueFactory(c -> c.getValue().enrolledProperty());
        enrollmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadRealData() {
        EnrollmentDAO dao = new EnrollmentDAO();
        List<EnrollmentDAO.EnrollmentDTO> list = dao.getAll();
        ObservableList<EnrollmentRow> rows = FXCollections.observableArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        ObservableList<String> distinctCourses = FXCollections.observableArrayList();
        distinctCourses.add("All Courses");

        for (EnrollmentDAO.EnrollmentDTO dto : list) {
            String enrolledStr = dto.enrollment.getEnrolledAt() != null ? sdf.format(dto.enrollment.getEnrolledAt())
                    : "N/A";
            double p = dto.enrollment.getProgressPercent();
            String progressStr = String.format("%.0f%%", p);
            String status = dto.enrollment.getStatus();

            // Translate status
            if (status == null)
                status = "Đang học";
            else if (status.equalsIgnoreCase("Active"))
                status = "Đang học";
            else if (status.equalsIgnoreCase("Completed"))
                status = "Hoàn thành";
            else if (status.equalsIgnoreCase("Paused"))
                status = "Tạm dừng";

            String cTitle = dto.courseTitle != null ? dto.courseTitle : "Unknown Course";
            if (!distinctCourses.contains(cTitle))
                distinctCourses.add(cTitle);

            rows.add(new EnrollmentRow(
                    dto.userName != null ? dto.userName : "N/A",
                    cTitle,
                    progressStr,
                    status,
                    enrolledStr,
                    new String[] { "Giới thiệu", "Cấu hình", "Thực hành" } // Demo list
            ));
        }

        allEnrollments = rows;
        filteredEnrollments = new FilteredList<>(allEnrollments, p -> true);
        enrollmentsTable.setItems(filteredEnrollments);
        updateCount();

        // Setup filter options
        courseFilter.setItems(distinctCourses);
        statusFilter.setItems(FXCollections.observableArrayList("All Status", "Đang học", "Hoàn thành", "Tạm dừng"));
        progressFilter
                .setItems(FXCollections.observableArrayList("All Progress", "0-25%", "26-50%", "51-75%", "76-100%"));
    }

    private void setupFilters() {
        courseFilter.valueProperty().addListener((o, a, b) -> applyFilters());
        statusFilter.valueProperty().addListener((o, a, b) -> applyFilters());
        progressFilter.valueProperty().addListener((o, a, b) -> applyFilters());
        searchField.textProperty().addListener((o, a, b) -> applyFilters());
    }

    private void applyFilters() {
        filteredEnrollments.setPredicate(enrollment -> {
            // Course filter
            String course = courseFilter.getValue();
            if (course != null && !course.equals("All Courses") && !enrollment.getCourse().contains(course)) {
                return false;
            }

            // Status filter
            String status = statusFilter.getValue();
            if (status != null && !status.equals("All Status") && !enrollment.getStatus().equals(status)) {
                return false;
            }

            // Progress filter
            String progress = progressFilter.getValue();
            if (progress != null && !progress.equals("All Progress")) {
                int p = Integer.parseInt(enrollment.getProgress().replace("%", ""));
                boolean match = switch (progress) {
                    case "0-25%" -> p <= 25;
                    case "26-50%" -> p > 25 && p <= 50;
                    case "51-75%" -> p > 50 && p <= 75;
                    case "76-100%" -> p > 75;
                    default -> true;
                };
                if (!match)
                    return false;
            }

            // Search filter
            String search = searchField.getText();
            if (search != null && !search.isEmpty()) {
                return enrollment.getUser().toLowerCase().contains(search.toLowerCase()) ||
                        enrollment.getCourse().toLowerCase().contains(search.toLowerCase());
            }

            return true;
        });
        updateCount();
    }

    private void setupTableSelection() {
        enrollmentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEnrollmentDetail(newVal);
            } else {
                clearDetailPanel();
            }
        });
    }

    private void showEnrollmentDetail(EnrollmentRow enrollment) {
        lblStudent.setText(enrollment.getUser());
        lblCourse.setText(enrollment.getCourse());
        lblStatus.setText(enrollment.getStatus());
        lblStatus.getStyleClass().removeAll("enrollments-status-active", "enrollments-status-completed",
                "enrollments-status-paused");
        String styleClass = switch (enrollment.getStatus()) {
            case "Đang học" -> "enrollments-status-active";
            case "Hoàn thành" -> "enrollments-status-completed";
            case "Tạm dừng" -> "enrollments-status-paused";
            default -> "enrollments-status-active";
        };
        lblStatus.getStyleClass().add(styleClass);

        lblProgress.setText(enrollment.getProgress());
        int progress = Integer.parseInt(enrollment.getProgress().replace("%", ""));
        progressBar.setProgress(progress / 100.0);

        lblEnrolled.setText(enrollment.getEnrolled());
        lblLastActivity.setText("Gần đây");

        // Show lessons
        String[] lessons = enrollment.getLessons();
        int completedCount = (int) (lessons.length * progress / 100.0);
        lblLessonCount.setText(completedCount + "/" + lessons.length);

        lessonsContainer.getChildren().clear();
        for (int i = 0; i < lessons.length; i++) {
            boolean completed = i < completedCount;
            lessonsContainer.getChildren().add(createLessonItem(lessons[i], completed, i + 1));
        }
    }

    private HBox createLessonItem(String lessonName, boolean completed, int number) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("enrollments-lesson-item");
        item.setPadding(new Insets(10, 14, 10, 14));

        Label numLabel = new Label(String.valueOf(number));
        numLabel.getStyleClass().add("enrollments-lesson-number");

        Label nameLabel = new Label(lessonName);
        nameLabel.getStyleClass().add("enrollments-lesson-name");
        HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

        Label statusLabel = new Label(completed ? "✓ Hoàn thành" : "⏳ Chưa học");
        statusLabel.getStyleClass().add(completed ? "enrollments-lesson-completed" : "enrollments-lesson-pending");

        item.getChildren().addAll(numLabel, nameLabel, statusLabel);
        return item;
    }

    private void clearDetailPanel() {
        lblStudent.setText("Chọn một đăng ký");
        lblCourse.setText("-");
        lblStatus.setText("-");
        lblProgress.setText("0%");
        progressBar.setProgress(0);
        lblEnrolled.setText("-");
        lblLastActivity.setText("-");
        lblLessonCount.setText("0/0");

        lessonsContainer.getChildren().clear();
        Label emptyMsg = new Label("Chọn một đăng ký để xem chi tiết bài học");
        emptyMsg.setWrapText(true);
        emptyMsg.getStyleClass().add("enrollments-empty-message");
        lessonsContainer.getChildren().add(emptyMsg);
    }

    private void updateCount() {
        countLabel.setText(filteredEnrollments.size() + " đăng ký");
    }

    // Model
    public static class EnrollmentRow {
        private final SimpleStringProperty user, course, progress, status, enrolled;
        private final String[] lessons;

        public EnrollmentRow(String user, String course, String progress, String status, String enrolled,
                String[] lessons) {
            this.user = new SimpleStringProperty(user);
            this.course = new SimpleStringProperty(course);
            this.progress = new SimpleStringProperty(progress);
            this.status = new SimpleStringProperty(status);
            this.enrolled = new SimpleStringProperty(enrolled);
            this.lessons = lessons;
        }

        public String getUser() {
            return user.get();
        }

        public String getCourse() {
            return course.get();
        }

        public String getProgress() {
            return progress.get();
        }

        public String getStatus() {
            return status.get();
        }

        public String getEnrolled() {
            return enrolled.get();
        }

        public String[] getLessons() {
            return lessons;
        }

        public SimpleStringProperty userProperty() {
            return user;
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

        public SimpleStringProperty enrolledProperty() {
            return enrolled;
        }
    }
}