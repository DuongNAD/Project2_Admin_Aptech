package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import com.elearning.admin.dao.ReviewDAO;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * Controller for Reviews management.
 * - Table: Course, User, Rating, Comment (short), CreatedAt
 * - Filter by course, rating, status
 * - Actions: hide / delete review
 */
public class ReviewsController {

    @FXML
    private ComboBox<String> courseFilter, ratingFilter, statusFilter;
    @FXML
    private TextField searchField;
    @FXML
    private Label countLabel;
    @FXML
    private TableView<ReviewRow> reviewsTable;
    @FXML
    private TableColumn<ReviewRow, String> courseCol, userCol, ratingCol, commentCol, createdCol, actionsCol;

    private ObservableList<ReviewRow> allReviews;
    private FilteredList<ReviewRow> filteredReviews;

    private static final int COMMENT_PREVIEW_LENGTH = 60;

    @FXML
    public void initialize() {
        setupTable();
        loadRealData();
        setupFilters();
    }

    private void setupTable() {
        courseCol.setCellValueFactory(c -> c.getValue().courseProperty());
        userCol.setCellValueFactory(c -> c.getValue().userProperty());
        ratingCol.setCellValueFactory(c -> c.getValue().ratingProperty());
        ratingCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(" " + item);
                    badge.getStyleClass().add("reviews-rating-badge");
                    int r = Integer.parseInt(item);
                    if (r >= 4)
                        badge.getStyleClass().add("reviews-rating-high");
                    else if (r >= 3)
                        badge.getStyleClass().add("reviews-rating-mid");
                    else
                        badge.getStyleClass().add("reviews-rating-low");
                    setGraphic(badge);
                }
            }
        });
        commentCol.setCellValueFactory(c -> c.getValue().commentProperty());
        commentCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String shortText = item.length() > COMMENT_PREVIEW_LENGTH
                            ? item.substring(0, COMMENT_PREVIEW_LENGTH) + "..."
                            : item;
                    setText(shortText);
                    setStyle("-fx-text-fill: #e5e7eb; -fx-font-size: 12px;");
                }
            }
        });
        createdCol.setCellValueFactory(c -> c.getValue().createdProperty());

        // Actions: Hide / Delete
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button btnHide = new Button("👁 Ẩn");
            private final Button btnDelete = new Button("🗑 Xóa");
            private final HBox box = new HBox(6, btnHide, btnDelete);

            {
                box.setAlignment(Pos.CENTER);
                btnHide.getStyleClass().add("reviews-btn-hide");
                btnDelete.getStyleClass().add("reviews-btn-delete");
                btnHide.setOnAction(e -> {
                    ReviewRow row = getTableView().getItems().get(getIndex());
                    hideReview(row);
                });
                btnDelete.setOnAction(e -> {
                    ReviewRow row = getTableView().getItems().get(getIndex());
                    deleteReview(row);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        reviewsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadRealData() {
        ReviewDAO dao = new ReviewDAO();
        List<ReviewDAO.ReviewDTO> list = dao.getAll();
        ObservableList<ReviewRow> rows = FXCollections.observableArrayList();
        ObservableList<String> distinctCourses = FXCollections.observableArrayList("All Courses");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (ReviewDAO.ReviewDTO dto : list) {
            String createdStr = dto.review.getCreatedAt() != null ? sdf.format(dto.review.getCreatedAt()) : "N/A";
            String cTitle = dto.courseTitle != null ? dto.courseTitle : "Unknown";
            if (!distinctCourses.contains(cTitle))
                distinctCourses.add(cTitle);

            rows.add(new ReviewRow(
                    dto.review.getReviewId(),
                    cTitle,
                    dto.userName != null ? dto.userName : "N/A",
                    String.valueOf(dto.review.getRating()),
                    dto.review.getComment() != null ? dto.review.getComment() : "",
                    createdStr,
                    true // Visible by default
            ));
        }

        allReviews = rows;
        filteredReviews = new FilteredList<>(allReviews, p -> true);
        reviewsTable.setItems(filteredReviews);
        updateCount();

        courseFilter.setItems(distinctCourses);
        ratingFilter.setItems(
                FXCollections.observableArrayList("All Ratings", "5 sao", "4 sao", "3 sao", "2 sao", "1 sao"));
        statusFilter.setItems(FXCollections.observableArrayList("All Status", "Hiển thị", "Đã ẩn"));
    }

    private void setupFilters() {
        courseFilter.valueProperty().addListener((o, a, b) -> applyFilters());
        ratingFilter.valueProperty().addListener((o, a, b) -> applyFilters());
        statusFilter.valueProperty().addListener((o, a, b) -> applyFilters());
        searchField.textProperty().addListener((o, a, b) -> applyFilters());
    }

    private void applyFilters() {
        filteredReviews.setPredicate(review -> {
            String course = courseFilter.getValue();
            if (course != null && !course.equals("All Courses") && !review.getCourse().equals(course))
                return false;
            String rating = ratingFilter.getValue();
            if (rating != null && !rating.equals("All Ratings")) {
                String expected = rating.substring(0, 1);
                if (!review.getRating().equals(expected))
                    return false;
            }
            String status = statusFilter.getValue();
            if (status != null && !status.equals("All Status")) {
                boolean visible = review.isVisible();
                if ("Hiển thị".equals(status) && !visible)
                    return false;
                if ("Đã ẩn".equals(status) && visible)
                    return false;
            }
            String search = searchField.getText();
            if (search != null && !search.isEmpty()) {
                if (!review.getComment().toLowerCase().contains(search.toLowerCase()) &&
                        !review.getUser().toLowerCase().contains(search.toLowerCase()))
                    return false;
            }
            return true;
        });
        updateCount();
    }

    private void hideReview(ReviewRow row) {
        row.setVisible(false);
        reviewsTable.refresh();
        updateCount();
        showAlert("Đã ẩn", "Đánh giá đã được ẩn khỏi công khai.");
    }

    private void deleteReview(ReviewRow row) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Xóa đánh giá vĩnh viễn");
        alert.setContentText("Hành động này không thể hoàn tác.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ReviewDAO dao = new ReviewDAO();
                if (dao.delete(row.getReviewId())) {
                    allReviews.remove(row);
                    updateCount();
                } else {
                    showAlert("Lỗi", "Không thể xóa đánh giá khỏi CSDL.");
                }
            }
        });
    }

    private void updateCount() {
        countLabel.setText(filteredReviews.size() + " đánh giá");
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public static class ReviewRow {
        private final int reviewId;
        private final SimpleStringProperty course, user, rating, comment, created;
        private boolean visible;

        public ReviewRow(int reviewId, String course, String user, String rating, String comment, String created,
                boolean visible) {
            this.reviewId = reviewId;
            this.course = new SimpleStringProperty(course);
            this.user = new SimpleStringProperty(user);
            this.rating = new SimpleStringProperty(rating);
            this.comment = new SimpleStringProperty(comment);
            this.created = new SimpleStringProperty(created);
            this.visible = visible;
        }

        public int getReviewId() {
            return reviewId;
        }

        public String getCourse() {
            return course.get();
        }

        public String getUser() {
            return user.get();
        }

        public String getRating() {
            return rating.get();
        }

        public String getComment() {
            return comment.get();
        }

        public String getCreated() {
            return created.get();
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean v) {
            visible = v;
        }

        public SimpleStringProperty courseProperty() {
            return course;
        }

        public SimpleStringProperty userProperty() {
            return user;
        }

        public SimpleStringProperty ratingProperty() {
            return rating;
        }

        public SimpleStringProperty commentProperty() {
            return comment;
        }

        public SimpleStringProperty createdProperty() {
            return created;
        }
    }
}