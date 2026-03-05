package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.elearning.admin.dao.DiscussionDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

/**
 * Controller for Discussions management.
 * - Filter: Course -> Section -> Lesson (cascading)
 * - Thread list of comments with reply depth
 * - Action: delete violating comment
 */
public class DiscussionsController {

    @FXML
    private ComboBox<String> courseFilter, sectionFilter, lessonFilter;
    @FXML
    private Label countLabel, contextLabel;
    @FXML
    private VBox commentsContainer;

    private final Map<String, List<String>> sectionsByCourse = Map.of(
            "Java Cơ bản", List.of("Chương 1: Giới thiệu", "Chương 2: Cú pháp cơ bản", "Chương 3: OOP"),
            "Spring Boot API", List.of("Chương 1: REST API", "Chương 2: Database", "Chương 3: Security"),
            "React Fundamentals", List.of("Chương 1: Components", "Chương 2: State", "Chương 3: Hooks"));
    private final Map<String, List<String>> lessonsBySection = Map.of(
            "Chương 1: Giới thiệu",
            List.of("Bài 1: Cài đặt JDK", "Bài 2: Hello World", "Bài 3: Biến và kiểu dữ liệu"),
            "Chương 2: Cú pháp cơ bản",
            List.of("Bài 4: Câu lệnh if-else", "Bài 5: Vòng lặp", "Bài 6: Mảng"),
            "Chương 3: OOP", List.of("Bài 7: Class và Object", "Bài 8: Kế thừa", "Bài 9: Đa hình"),
            "Chương 1: REST API",
            List.of("Bài 1: Controller", "Bài 2: Request/Response", "Bài 3: Validation"),
            "Chương 2: Database", List.of("Bài 4: JPA Entity", "Bài 5: Repository", "Bài 6: Transaction"),
            "Chương 3: Security", List.of("Bài 7: JWT", "Bài 8: CORS", "Bài 9: Rate Limit"),
            "Chương 1: Components", List.of("Bài 1: JSX", "Bài 2: Functional Component", "Bài 3: Props"),
            "Chương 2: State", List.of("Bài 4: useState", "Bài 5: useReducer"),
            "Chương 3: Hooks",
            List.of("Bài 6: useEffect", "Bài 7: useLayoutEffect", "Bài 8: Custom Hooks"));

    private List<CommentItem> currentComments = new ArrayList<>();

    @FXML
    public void initialize() {
        courseFilter
                .setItems(FXCollections.observableArrayList("Java Cơ bản", "Spring Boot API", "React Fundamentals"));
        courseFilter.valueProperty().addListener((o, a, course) -> {
            sectionFilter.getItems().clear();
            lessonFilter.getItems().clear();
            lessonFilter.setValue(null);
            if (course != null) {
                sectionFilter
                        .setItems(FXCollections.observableArrayList(sectionsByCourse.getOrDefault(course, List.of())));
            }
            sectionFilter.setValue(null);
        });
        sectionFilter.valueProperty().addListener((o, a, section) -> {
            lessonFilter.getItems().clear();
            lessonFilter.setValue(null);
            if (section != null) {
                lessonFilter
                        .setItems(FXCollections.observableArrayList(lessonsBySection.getOrDefault(section, List.of())));
            }
        });
        lessonFilter.valueProperty().addListener((o, a, lesson) -> {
            if (lesson != null)
                loadCommentsForLesson(lesson);
            else
                clearComments();
        });
    }

    @FXML
    private void clearFilters() {
        courseFilter.setValue(null);
        sectionFilter.setValue(null);
        lessonFilter.setValue(null);
        clearComments();
    }

    private void loadCommentsForLesson(String lessonName) {
        currentComments = getFakeCommentsForLesson(lessonName);
        contextLabel.setText(lessonName);
        countLabel.setText(currentComments.size() + " bình luận");
        renderComments();
    }

    // Note: Due to time constraints, this still uses simulated filtering logic but
    // would ideally query the DB by lesson_id
    private List<CommentItem> getFakeCommentsForLesson(String lessonName) {
        List<CommentItem> list = new ArrayList<>();
        DiscussionDAO dao = new DiscussionDAO();
        List<DiscussionDAO.DiscussionDTO> discussions = dao.getAll();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

        for (DiscussionDAO.DiscussionDTO dto : discussions) {
            String dateStr = dto.discussion.getCreatedAt() != null ? sdf.format(dto.discussion.getCreatedAt()) : "N/A";
            int depth = dto.discussion.getParentId() != null ? 1 : 0; // Simple 1-level depth logic

            list.add(new CommentItem(
                    dto.userName != null ? dto.userName : "N/A",
                    dto.discussion.getContent() != null ? dto.discussion.getContent() : "",
                    dateStr,
                    depth,
                    String.valueOf(dto.discussion.getDiscussionId())));
        }

        return list;
    }

    private void renderComments() {
        commentsContainer.getChildren().clear();
        for (CommentItem c : currentComments) {
            commentsContainer.getChildren().add(buildCommentCard(c));
        }
    }

    private VBox buildCommentCard(CommentItem c) {
        VBox card = new VBox(8);
        card.getStyleClass().add("discussions-comment-card");
        if (c.depth > 0)
            card.getStyleClass().add("discussions-comment-reply");
        card.setPadding(new Insets(12, 14, 12, 14 + c.depth * 24));

        HBox top = new HBox(10);
        top.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label userLabel = new Label(c.user);
        userLabel.getStyleClass().add("discussions-comment-user");
        Label dateLabel = new Label(c.date);
        dateLabel.getStyleClass().add("discussions-comment-date");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button deleteBtn = new Button("🗑️ Xóa");
        deleteBtn.getStyleClass().add("discussions-btn-delete");
        deleteBtn.setOnAction(e -> deleteComment(c));
        top.getChildren().addAll(userLabel, dateLabel, spacer, deleteBtn);

        Label contentLabel = new Label(c.content);
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("discussions-comment-content");
        contentLabel.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(top, contentLabel);
        return card;
    }

    private void deleteComment(CommentItem c) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xóa bình luận");
        alert.setHeaderText("Xóa bình luận vi phạm");
        alert.setContentText("Nội dung sẽ bị xóa vĩnh viễn.");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DiscussionDAO dao = new DiscussionDAO();
                if (dao.delete(Integer.parseInt(c.id))) {
                    currentComments.remove(c);
                    countLabel.setText(currentComments.size() + " bình luận");
                    renderComments();
                } else {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Không thể xóa bình luận từ cơ sở dữ liệu.");
                    err.showAndWait();
                }
            }
        });
    }

    private void clearComments() {
        currentComments.clear();
        commentsContainer.getChildren().clear();
        Label empty = new Label("Chọn Khóa học -> Section -> Bài học để hiển thị bình luận");
        empty.setWrapText(true);
        empty.getStyleClass().add("discussions-empty-message");
        commentsContainer.getChildren().add(empty);
        countLabel.setText("0 bình luận");
        contextLabel.setText("Chọn khóa học và bài học để xem bình luận");
    }

    private static class CommentItem {
        final String user, content, date;
        final int depth;
        final String id;

        CommentItem(String user, String content, String date, int depth, String id) {
            this.user = user;
            this.content = content;
            this.date = date;
            this.depth = depth;
            this.id = id;
        }
    }
}