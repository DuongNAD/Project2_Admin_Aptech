package com.elearning.admin.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;

import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Controller cho màn hình Chi tiết khóa học.
 * Thông tin chung + Tabs: Sections/Lessons, Enrollments, Reviews.
 */
public class CourseDetailController {

    @FXML
    private TextField titleField, subtitleField, priceField, languageField, instructorField;
    @FXML
    private ComboBox<String> levelField, statusField, categoryField;
    @FXML
    private TabPane detailTabs;
    @FXML
    private Label lessonsLabel;
    // Builder UI
    @FXML private ListView<Section> sectionsList;
    @FXML private ListView<Lesson> lessonsList;
    @FXML private Label selectedSectionLabel, editorStateLabel;
    @FXML private TextField lessonTitleField, lessonDurationField;
    @FXML private ComboBox<String> lessonTypeField;
    @FXML private CheckBox lessonPreviewCheck;
    @FXML private HTMLEditor lessonHtmlEditor;
    @FXML
    private TableView<EnrollmentRow> enrollmentsTable;
    @FXML
    private TableColumn<EnrollmentRow, String> enrollStudentCol;
    @FXML
    private TableColumn<EnrollmentRow, String> enrollDateCol;
    @FXML
    private TableColumn<EnrollmentRow, String> enrollProgressCol;
    @FXML
    private TableView<ReviewRow> reviewsTable;
    @FXML
    private TableColumn<ReviewRow, String> reviewUserCol;
    @FXML
    private TableColumn<ReviewRow, String> reviewRatingCol;
    @FXML
    private TableColumn<ReviewRow, String> reviewCommentCol;
    @FXML
    private TableColumn<ReviewRow, String> reviewDateCol;

    private Runnable onBack;
    private CoursesController.CourseRow course;
    private boolean newCourse;

    // per-course in-memory builder data
    private static final WeakHashMap<CoursesController.CourseRow, CourseContent> CONTENT = new WeakHashMap<>();
    private CourseContent content;
    private Lesson editingLesson;

    public void setCourse(CoursesController.CourseRow course) {
        this.course = course;
        refresh();
    }

    public void setNewCourse(boolean isNew) {
        this.newCourse = isNew;
    }

    public void setOnBack(Runnable r) {
        this.onBack = r;
    }

    @FXML
    private void initialize() {
        // các lựa chọn mặc định cho combo
        levelField.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        statusField.setItems(FXCollections.observableArrayList("Draft", "Published", "Archived"));
        categoryField.setItems(FXCollections.observableArrayList(
                "Programming", "Backend", "Frontend", "Data", "Design"
        ));

        // lesson editor options
        if (lessonTypeField != null) {
            lessonTypeField.setItems(FXCollections.observableArrayList("Video", "Article", "Quiz"));
        }

        setupBuilderUI();
    }

    private void setupBuilderUI() {
        if (sectionsList == null || lessonsList == null) return;

        sectionsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lessonsList.setItems(newVal.lessons);
                selectedSectionLabel.setText(newVal.title.get());
            } else {
                lessonsList.setItems(FXCollections.observableArrayList());
                selectedSectionLabel.setText("Chưa chọn section");
            }
            updateLessonSummaryLabel();
        });

        lessonsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                startEditingLesson(newVal);
            } else {
                clearEditor();
            }
        });

        // nicer rendering
        sectionsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Section item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                String badge = " (" + item.lessons.size() + " bài)";
                setText(item.title.get() + badge);
                getStyleClass().add("courses-builder-cell");
            }
        });

        lessonsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Lesson item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                String type = item.type.get() == null ? "Video" : item.type.get();
                String dur = item.duration.get() == null || item.duration.get().isBlank() ? "" : (" • " + item.duration.get() + "m");
                String preview = item.preview.get() ? " • Free" : "";
                setText(item.title.get() + "  [" + type + "]" + dur + preview);
                getStyleClass().add("courses-builder-cell");
            }
        });

        clearEditor();
    }

    private void refresh() {
        if (course == null) return;

        // attach or create content
        content = CONTENT.computeIfAbsent(course, c -> CourseContent.createDefault());
        if (sectionsList != null) {
            sectionsList.setItems(content.sections);
            if (!content.sections.isEmpty()) {
                sectionsList.getSelectionModel().select(0);
            }
        }

        // header fields
        titleField.setText(course.getTitle());
        subtitleField.setText("Khóa học " + course.getCategory() + " do " + course.instructorProperty().get() + " giảng dạy.");

        // basic info form
        priceField.setText(course.priceProperty().get());
        languageField.setText("Tiếng Việt");
        instructorField.setText(course.instructorProperty().get());
        categoryField.setValue(course.getCategory());

        if (course.getStatus() != null) {
            statusField.setValue(course.getStatus());
        } else {
            statusField.setValue("Draft");
        }
        // tạm hard-code level
        levelField.setValue("Beginner");

        loadEnrollments();
        loadReviews();
        updateLessonSummaryLabel();
    }

    private void updateLessonSummaryLabel() {
        if (lessonsLabel == null || content == null) return;
        int sectionCount = content.sections.size();
        int lessonCount = content.sections.stream().mapToInt(s -> s.lessons.size()).sum();
        lessonsLabel.setText(sectionCount + " phần • " + lessonCount + " bài học");
        // force refresh cells to update badges
        if (sectionsList != null) sectionsList.refresh();
    }

    private void loadEnrollments() {
        enrollStudentCol.setCellValueFactory(c -> c.getValue().studentProperty());
        enrollDateCol.setCellValueFactory(c -> c.getValue().dateProperty());
        enrollProgressCol.setCellValueFactory(c -> c.getValue().progressProperty());

        ObservableList<EnrollmentRow> rows = FXCollections.observableArrayList(
                new EnrollmentRow("Nguyễn Văn An", "20/01/2025", "45%"),
                new EnrollmentRow("Trần Thị Bình", "18/01/2025", "80%"),
                new EnrollmentRow("Lê Văn Cường", "15/01/2025", "100%"),
                new EnrollmentRow("Phạm Thị Dung", "22/01/2025", "12%")
        );
        enrollmentsTable.setItems(rows);
    }

    private void loadReviews() {
        reviewUserCol.setCellValueFactory(c -> c.getValue().userProperty());
        reviewRatingCol.setCellValueFactory(c -> c.getValue().ratingProperty());
        reviewCommentCol.setCellValueFactory(c -> c.getValue().commentProperty());
        reviewDateCol.setCellValueFactory(c -> c.getValue().dateProperty());

        ObservableList<ReviewRow> rows = FXCollections.observableArrayList(
                new ReviewRow("Nguyễn Văn An", "5", "Khóa học rất hay, dễ hiểu!", "21/01/2025"),
                new ReviewRow("Trần Thị Bình", "4", "Nội dung tốt, giá hợp lý.", "19/01/2025"),
                new ReviewRow("Lê Văn Cường", "5", "Hoàn thành khóa, rất hài lòng.", "17/01/2025")
        );
        reviewsTable.setItems(rows);
    }

    @FXML
    private void onBackClicked() {
        if (onBack != null) onBack.run();
    }

    @FXML
    private void onSaveClicked() {
        if (course == null) return;

        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String subtitle = subtitleField.getText() == null ? "" : subtitleField.getText().trim();
        String price = priceField.getText() == null ? "" : priceField.getText().trim();
        String language = languageField.getText() == null ? "" : languageField.getText().trim();
        String instructor = instructorField.getText() == null ? "" : instructorField.getText().trim();
        String category = categoryField.getValue();
        String status = statusField.getValue();
        String level = levelField.getValue();

        if (title.isEmpty()) {
            showAlert("Thiếu thông tin", "Tên khóa học không được để trống.");
            return;
        }
        if (category == null || category.isBlank()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn danh mục cho khóa học.");
            return;
        }

        // cập nhật lại CourseRow để list phía ngoài thấy thay đổi
        course.titleProperty().set(title);
        course.categoryProperty().set(category);
        course.instructorProperty().set(instructor.isEmpty() ? "Admin" : instructor);
        course.priceProperty().set(price.isEmpty() ? "0 VNĐ" : price);
        if (status != null) {
            course.statusProperty().set(status);
        }

        // cập nhật lại subtitle hiển thị
        if (subtitle.isEmpty()) {
            subtitleField.setText("Khóa học " + category + " do " + course.instructorProperty().get() + " giảng dạy.");
        }

        showAlert("Đã lưu", newCourse ? "Khóa học mới đã được lưu bản nháp." : "Đã cập nhật thông tin khóa học.");
    }

    @FXML
    private void onPublishClicked() {
        if (course == null) return;
        statusField.setValue("Published");
        onSaveClicked();
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    /* ===== Builder actions ===== */

    @FXML
    private void onAddSectionClicked() {
        if (content == null) return;
        TextInputDialog d = new TextInputDialog("Section mới");
        d.setTitle("Thêm Section");
        d.setHeaderText("Nhập tên section");
        d.setContentText("Tên:");
        d.showAndWait().ifPresent(name -> {
            String t = name.trim();
            if (t.isEmpty()) return;
            Section s = new Section(t);
            content.sections.add(s);
            if (sectionsList != null) {
                sectionsList.getSelectionModel().select(s);
            }
            updateLessonSummaryLabel();
        });
    }

    @FXML
    private void onRenameSectionClicked() {
        Section s = sectionsList == null ? null : sectionsList.getSelectionModel().getSelectedItem();
        if (s == null) {
            showAlert("Thiếu lựa chọn", "Hãy chọn một section để đổi tên.");
            return;
        }
        TextInputDialog d = new TextInputDialog(s.title.get());
        d.setTitle("Đổi tên Section");
        d.setHeaderText("Cập nhật tên section");
        d.setContentText("Tên:");
        d.showAndWait().ifPresent(name -> {
            String t = name.trim();
            if (!t.isEmpty()) {
                s.title.set(t);
                sectionsList.refresh();
            }
        });
    }

    @FXML
    private void onDeleteSectionClicked() {
        Section s = sectionsList == null ? null : sectionsList.getSelectionModel().getSelectedItem();
        if (s == null) {
            showAlert("Thiếu lựa chọn", "Hãy chọn một section để xóa.");
            return;
        }
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle("Xóa Section");
        a.setHeaderText("Xóa section \"" + s.title.get() + "\"");
        a.setContentText("Tất cả bài giảng trong section này cũng sẽ bị xóa.");
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                content.sections.remove(s);
                updateLessonSummaryLabel();
                clearEditor();
            }
        });
    }

    @FXML
    private void onAddLessonClicked() {
        Section s = sectionsList == null ? null : sectionsList.getSelectionModel().getSelectedItem();
        if (s == null) {
            showAlert("Thiếu lựa chọn", "Hãy chọn một section trước khi thêm bài giảng.");
            return;
        }
        Dialog<Lesson> dialog = new Dialog<>();
        dialog.setTitle("Thêm Bài giảng");
        dialog.setHeaderText("Tạo bài giảng mới");
        ButtonType createBtn = new ButtonType("Tạo", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        TextField title = new TextField();
        title.setPromptText("Tiêu đề bài giảng");
        ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList("Video", "Article", "Quiz"));
        type.setValue("Video");

        VBox box = new VBox(10,
                new Label("Tiêu đề *"), title,
                new Label("Loại"), type
        );
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(bt -> {
            if (bt == createBtn) {
                String t = title.getText() == null ? "" : title.getText().trim();
                if (t.isEmpty()) return null;
                Lesson l = new Lesson(t);
                l.type.set(type.getValue());
                return l;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(lesson -> {
            s.lessons.add(lesson);
            lessonsList.getSelectionModel().select(lesson);
            updateLessonSummaryLabel();
        });
    }

    @FXML
    private void onDeleteLessonClicked() {
        Lesson l = lessonsList == null ? null : lessonsList.getSelectionModel().getSelectedItem();
        Section s = sectionsList == null ? null : sectionsList.getSelectionModel().getSelectedItem();
        if (s == null || l == null) {
            showAlert("Thiếu lựa chọn", "Hãy chọn một bài giảng để xóa.");
            return;
        }
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle("Xóa Bài giảng");
        a.setHeaderText("Xóa bài giảng \"" + l.title.get() + "\"");
        a.setContentText("Hành động này không thể hoàn tác.");
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                s.lessons.remove(l);
                clearEditor();
                updateLessonSummaryLabel();
            }
        });
    }

    private void startEditingLesson(Lesson l) {
        editingLesson = l;
        editorStateLabel.setText("Đang chỉnh: " + l.title.get());
        lessonTitleField.setText(l.title.get());
        lessonTypeField.setValue(l.type.get());
        lessonDurationField.setText(l.duration.get());
        lessonPreviewCheck.setSelected(l.preview.get());
        if (lessonHtmlEditor != null) {
            String html = l.content.get();
            if (html == null || html.isBlank()) {
                html = "<p>" + escapeHtml(l.title.get()) + "</p>";
            }
            lessonHtmlEditor.setHtmlText(html);
        }
    }

    @FXML
    private void onSaveLessonClicked() {
        if (editingLesson == null) {
            showAlert("Chưa chọn bài", "Hãy chọn một bài giảng để lưu.");
            return;
        }
        String t = lessonTitleField.getText() == null ? "" : lessonTitleField.getText().trim();
        if (t.isEmpty()) {
            showAlert("Thiếu thông tin", "Tiêu đề bài giảng không được để trống.");
            return;
        }
        editingLesson.title.set(t);
        editingLesson.type.set(lessonTypeField.getValue() == null ? "Video" : lessonTypeField.getValue());
        editingLesson.duration.set(lessonDurationField.getText() == null ? "" : lessonDurationField.getText().trim());
        editingLesson.preview.set(lessonPreviewCheck.isSelected());
        if (lessonHtmlEditor != null) {
            editingLesson.content.set(lessonHtmlEditor.getHtmlText());
        }
        lessonsList.refresh();
        sectionsList.refresh();
        updateLessonSummaryLabel();
        showAlert("Đã lưu", "Đã lưu bài giảng.");
    }

    @FXML
    private void onCancelLessonEditClicked() {
        // reset editor fields from selected lesson (or clear)
        Lesson l = lessonsList == null ? null : lessonsList.getSelectionModel().getSelectedItem();
        if (l != null) startEditingLesson(l);
        else clearEditor();
    }

    @FXML
    private void onInsertImageClicked() {
        if (lessonHtmlEditor == null) return;
        TextInputDialog d = new TextInputDialog("https://");
        d.setTitle("Chèn ảnh");
        d.setHeaderText("Nhập URL ảnh để chèn vào nội dung bài giảng");
        d.setContentText("URL Image:");
        d.showAndWait().ifPresent(url -> {
            String u = url.trim();
            if (u.isEmpty() || u.equals("https://")) return;
            String html = lessonHtmlEditor.getHtmlText();
            String imgTag = "<p><img src=\"" + escapeHtml(u) + "\" style=\"max-width: 100%;\"/></p>";
            lessonHtmlEditor.setHtmlText(html + imgTag);
        });
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void clearEditor() {
        editingLesson = null;
        if (editorStateLabel != null) editorStateLabel.setText("Chưa chọn bài giảng");
        if (lessonTitleField != null) lessonTitleField.clear();
        if (lessonTypeField != null) lessonTypeField.setValue("Video");
        if (lessonDurationField != null) lessonDurationField.clear();
        if (lessonPreviewCheck != null) lessonPreviewCheck.setSelected(false);
        if (lessonHtmlEditor != null) lessonHtmlEditor.setHtmlText("");
    }

    public static class EnrollmentRow {
        private final SimpleStringProperty student, date, progress;

        public EnrollmentRow(String student, String date, String progress) {
            this.student = new SimpleStringProperty(student);
            this.date = new SimpleStringProperty(date);
            this.progress = new SimpleStringProperty(progress);
        }

        public SimpleStringProperty studentProperty() { return student; }
        public SimpleStringProperty dateProperty() { return date; }
        public SimpleStringProperty progressProperty() { return progress; }
    }

    public static class ReviewRow {
        private final SimpleStringProperty user, rating, comment, date;

        public ReviewRow(String user, String rating, String comment, String date) {
            this.user = new SimpleStringProperty(user);
            this.rating = new SimpleStringProperty(rating);
            this.comment = new SimpleStringProperty(comment);
            this.date = new SimpleStringProperty(date);
        }

        public SimpleStringProperty userProperty() { return user; }
        public SimpleStringProperty ratingProperty() { return rating; }
        public SimpleStringProperty commentProperty() { return comment; }
        public SimpleStringProperty dateProperty() { return date; }
    }

    /* ===== In-memory models ===== */
    private static class CourseContent {
        final ObservableList<Section> sections = FXCollections.observableArrayList();

        static CourseContent createDefault() {
            CourseContent c = new CourseContent();
            Section s1 = new Section("Section 1 : Giới thiệu");
            s1.lessons.addAll(
                    new Lesson("Bài 1 : Tổng quan"),
                    new Lesson("Bài 2 : Cài đặt môi trường")
            );
            Section s2 = new Section("Section 2 : Nội dung chính");
            s2.lessons.addAll(
                    new Lesson("Bài 3 : Kiến thức nền"),
                    new Lesson("Bài 4 : Thực hành"),
                    new Lesson("Bài 5 : Bài tập")
            );
            c.sections.addAll(s1, s2);
            return c;
        }
    }

    public static class Section {
        final String id = UUID.randomUUID().toString();
        final SimpleStringProperty title = new SimpleStringProperty();
        final ObservableList<Lesson> lessons = FXCollections.observableArrayList();

        Section(String title) {
            this.title.set(title);
        }

        @Override
        public String toString() {
            return title.get();
        }
    }

    public static class Lesson {
        final String id = UUID.randomUUID().toString();
        final SimpleStringProperty title = new SimpleStringProperty();
        final SimpleStringProperty type = new SimpleStringProperty("Video");
        final SimpleStringProperty duration = new SimpleStringProperty("");
        final javafx.beans.property.SimpleBooleanProperty preview = new javafx.beans.property.SimpleBooleanProperty(false);
        final SimpleStringProperty content = new SimpleStringProperty("");

        Lesson(String title) {
            this.title.set(title);
        }

        @Override
        public String toString() {
            return title.get();
        }
    }
}