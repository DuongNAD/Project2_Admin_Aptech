package com.elearning.admin.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.elearning.admin.dao.CourseDAO;
import com.elearning.admin.dao.SectionDAO;
import com.elearning.admin.dao.LessonDAO;
import com.elearning.admin.dao.CategoryDAO;
import com.elearning.admin.dao.EnrollmentDAO;
import com.elearning.admin.dao.ReviewDAO;
import com.elearning.admin.models.Course;
import com.elearning.admin.models.Section;
import com.elearning.admin.models.Lesson;
import com.elearning.admin.models.Category;
import java.math.BigDecimal;

public class CourseDetailController {

    @FXML
    private TextField titleField, subtitleField, priceField, languageField, instructorField;
    @FXML
    private ComboBox<String> levelField, statusField, categoryField;
    @FXML
    private TabPane detailTabs;
    @FXML
    private Label lessonsLabel;
    @FXML
    private Label statLessonsLabel, statEnrollmentsLabel, statRatingLabel;
    @FXML
    private Label tabEnrollmentsCountLabel, tabReviewsCountLabel;

    @FXML
    private ListView<SectionWrapper> sectionsList;
    @FXML
    private ListView<LessonWrapper> lessonsList;
    @FXML
    private Label selectedSectionLabel, editorStateLabel;
    @FXML
    private TextField lessonTitleField, lessonDurationField;
    @FXML
    private ComboBox<String> lessonTypeField;
    @FXML
    private CheckBox lessonPreviewCheck;
    @FXML
    private HTMLEditor lessonHtmlEditor;

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
    private CoursesController.CourseRow courseRow;
    private boolean newCourse;
    private boolean readOnly;

    private Course dbCourse;
    private ObservableList<SectionWrapper> contentSections = FXCollections.observableArrayList();
    private List<SectionWrapper> deletedSections = new ArrayList<>();
    private List<LessonWrapper> deletedLessons = new ArrayList<>();
    private LessonWrapper editingLesson;
    private List<Category> allCategories;

    public void setCourse(CoursesController.CourseRow course) {
        this.courseRow = course;
        refresh();
    }

    public void setNewCourse(boolean isNew) {
        this.newCourse = isNew;
    }

    public void setReadOnly(boolean isReadOnly) {
        this.readOnly = isReadOnly;

        // Disable fields if read only (null-safe in case called early)
        if (titleField != null)
            titleField.setEditable(!readOnly);
        if (subtitleField != null)
            subtitleField.setEditable(!readOnly);
        if (priceField != null)
            priceField.setEditable(!readOnly);
        if (languageField != null)
            languageField.setEditable(!readOnly);
        if (instructorField != null)
            instructorField.setEditable(!readOnly);

        if (categoryField != null)
            categoryField.setDisable(readOnly);
        if (statusField != null)
            statusField.setDisable(readOnly);
        if (levelField != null)
            levelField.setDisable(readOnly);
    }

    public void setOnBack(Runnable r) {
        this.onBack = r;
    }

    @FXML
    private void initialize() {
        levelField.setItems(FXCollections.observableArrayList("Beginner", "Intermediate", "Advanced"));
        statusField.setItems(FXCollections.observableArrayList("Draft", "Published", "Archived"));

        allCategories = new CategoryDAO().getAll();
        ObservableList<String> catNames = FXCollections.observableArrayList();
        for (Category c : allCategories) {
            catNames.add(c.getName());
        }
        categoryField.setItems(catNames);

        if (lessonTypeField != null) {
            lessonTypeField.setItems(FXCollections.observableArrayList("video", "article", "quiz"));
        }

        setupBuilderUI();
    }

    private void setupBuilderUI() {
        if (sectionsList == null || lessonsList == null)
            return;

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

        sectionsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(SectionWrapper item, boolean empty) {
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
            protected void updateItem(LessonWrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                String type = item.type.get() == null ? "video" : item.type.get();
                String dur = item.duration.get() == null || item.duration.get().isBlank() ? ""
                        : (" • " + item.duration.get() + "m");
                String preview = item.preview.get() ? " • Free" : "";
                setText(item.title.get() + "  [" + type + "]" + dur + preview);
                getStyleClass().add("courses-builder-cell");
            }
        });

        // Defer clearEditor() so HTMLEditor WebKit is fully initialized first
        Platform.runLater(this::clearEditor);
    }

    private void refresh() {
        if (courseRow == null)
            return;

        dbCourse = new Course();
        contentSections.clear();
        deletedSections.clear();
        deletedLessons.clear();

        if (!newCourse && courseRow.getCourseId() > 0) {
            CourseDAO courseDAO = new CourseDAO();
            dbCourse = courseDAO.getById(courseRow.getCourseId());
            if (dbCourse == null) {
                dbCourse = new Course();
                dbCourse.setCourseId(courseRow.getCourseId());
            }

            SectionDAO secDAO = new SectionDAO();
            LessonDAO lesDAO = new LessonDAO();

            List<Section> secs = secDAO.getSectionsByCourseId(dbCourse.getCourseId());
            for (Section s : secs) {
                SectionWrapper sw = new SectionWrapper(s);
                List<Lesson> less = lesDAO.getLessonsBySectionId(s.getSectionId());
                for (Lesson l : less) {
                    sw.lessons.add(new LessonWrapper(l));
                }
                contentSections.add(sw);
            }
        }

        if (sectionsList != null) {
            sectionsList.setItems(contentSections);
            if (!contentSections.isEmpty()) {
                sectionsList.getSelectionModel().select(0);
            }
        }

        if (dbCourse.getTitle() != null) {
            titleField.setText(dbCourse.getTitle());
        } else {
            titleField.setText(courseRow.getTitle());
        }

        if (dbCourse.getSubtitle() != null) {
            subtitleField.setText(dbCourse.getSubtitle());
        } else {
            subtitleField.setText(
                    "Khóa học " + courseRow.getCategory() + " do " + courseRow.instructorProperty().get()
                            + " giảng dạy.");
        }

        if (dbCourse.getPrice() != null) {
            priceField.setText(String.format("%.0f", dbCourse.getPrice().doubleValue()));
        } else {
            String priceText = courseRow.priceProperty().get().replaceAll("[^0-9]", "");
            if (priceText.isEmpty())
                priceText = "0";
            priceField.setText(priceText);
        }

        if (dbCourse.getLanguage() != null) {
            languageField.setText(dbCourse.getLanguage());
        } else {
            languageField.setText("Vietnamese");
        }
        instructorField.setText(courseRow.instructorProperty().get());
        categoryField.setValue(courseRow.getCategory());

        if (dbCourse.getStatus() != null) {
            statusField.setValue(dbCourse.getStatus().equalsIgnoreCase("active")
                    || dbCourse.getStatus().equalsIgnoreCase("published") ? "Published" : "Draft");
        } else if (courseRow.getStatus() != null) {
            statusField.setValue(courseRow.getStatus().equalsIgnoreCase("active")
                    || courseRow.getStatus().equalsIgnoreCase("published") ? "Published" : "Draft");
        } else {
            statusField.setValue("Draft");
        }

        if (dbCourse.getLevel() != null) {
            levelField.setValue(dbCourse.getLevel());
        } else {
            levelField.setValue("Beginner");
        }

        loadEnrollments();
        loadReviews();
        updateLessonSummaryLabel();
    }

    private void updateLessonSummaryLabel() {
        if (lessonsLabel == null)
            return;
        int sectionCount = contentSections.size();
        int lessonCount = contentSections.stream().mapToInt(s -> s.lessons.size()).sum();
        lessonsLabel.setText(sectionCount + " phần • " + lessonCount + " bài học");
        if (statLessonsLabel != null)
            statLessonsLabel.setText(String.valueOf(lessonCount));
        if (sectionsList != null)
            sectionsList.refresh();
    }

    private void loadEnrollments() {
        enrollStudentCol.setCellValueFactory(c -> c.getValue().studentProperty());
        enrollDateCol.setCellValueFactory(c -> c.getValue().dateProperty());
        enrollProgressCol.setCellValueFactory(c -> c.getValue().progressProperty());

        ObservableList<EnrollmentRow> rows = FXCollections.observableArrayList();
        int count = 0;
        if (dbCourse != null && dbCourse.getCourseId() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            EnrollmentDAO enrDAO = new EnrollmentDAO();
            List<EnrollmentDAO.EnrollmentDTO> enrList = enrDAO.getByCourseId(dbCourse.getCourseId());
            count = enrList.size();
            for (EnrollmentDAO.EnrollmentDTO dto : enrList) {
                String dateStr = dto.enrollment.getEnrolledAt() != null
                        ? sdf.format(dto.enrollment.getEnrolledAt())
                        : "N/A";
                String progress = String.format("%.0f%%", dto.enrollment.getProgressPercent());
                rows.add(new EnrollmentRow(dto.userName, dateStr, progress));
            }
        }
        enrollmentsTable.setItems(rows);
        if (statEnrollmentsLabel != null)
            statEnrollmentsLabel.setText(String.valueOf(count));
        if (tabEnrollmentsCountLabel != null)
            tabEnrollmentsCountLabel.setText(count + " học viên");
    }

    private void loadReviews() {
        reviewUserCol.setCellValueFactory(c -> c.getValue().userProperty());
        reviewRatingCol.setCellValueFactory(c -> c.getValue().ratingProperty());
        reviewCommentCol.setCellValueFactory(c -> c.getValue().commentProperty());
        reviewDateCol.setCellValueFactory(c -> c.getValue().dateProperty());

        ObservableList<ReviewRow> rows = FXCollections.observableArrayList();
        int count = 0;
        double avgRating = 0.0;
        if (dbCourse != null && dbCourse.getCourseId() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            ReviewDAO revDAO = new ReviewDAO();
            List<ReviewDAO.ReviewDTO> revList = revDAO.getByCourseId(dbCourse.getCourseId());
            count = revList.size();
            double totalStars = 0;
            for (ReviewDAO.ReviewDTO dto : revList) {
                totalStars += dto.review.getRating();
                String dateStr = dto.review.getCreatedAt() != null
                        ? sdf.format(dto.review.getCreatedAt())
                        : "N/A";
                String rating = String.valueOf(dto.review.getRating()) + " ⭐";
                String comment = dto.review.getComment() != null ? dto.review.getComment() : "";
                rows.add(new ReviewRow(dto.userName, rating, comment, dateStr));
            }
            if (count > 0) {
                avgRating = totalStars / count;
            }
        }
        reviewsTable.setItems(rows);
        String ratingStr = String.format(java.util.Locale.US, "%.1f", avgRating);
        if (statRatingLabel != null)
            statRatingLabel.setText(ratingStr);
        if (tabReviewsCountLabel != null)
            tabReviewsCountLabel.setText(count + " đánh giá • " + ratingStr + " ⭐");
    }

    @FXML
    private void onBackClicked() {
        if (onBack != null)
            onBack.run();
    }

    @FXML
    private void onSaveClicked() {
        if (readOnly) {
            showAlert("Chế độ xem", "Bạn đang ở chế độ xem chi tiết. Không thể lưu thay đổi.");
            return;
        }
        if (courseRow == null)
            return;

        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String subtitle = subtitleField.getText() == null ? "" : subtitleField.getText().trim();
        String priceStr = priceField.getText() == null ? "0" : priceField.getText().trim().replaceAll("[^0-9]", "");
        String language = languageField.getText() == null ? "Vietnamese" : languageField.getText().trim();
        String category = categoryField.getValue();
        String status = statusField.getValue() != null ? statusField.getValue() : "Draft";
        String level = levelField.getValue() != null ? levelField.getValue() : "Beginner";

        if (title.isEmpty()) {
            showAlert("Thiếu thông tin", "Tên khóa học không được để trống.");
            return;
        }
        if (category == null || category.isBlank()) {
            showAlert("Thiếu thông tin", "Vui lòng chọn danh mục cho khóa học.");
            return;
        }

        dbCourse.setTitle(title);
        dbCourse.setSubtitle(subtitle);
        dbCourse.setPrice(new BigDecimal(priceStr.isEmpty() ? "0" : priceStr));
        dbCourse.setLanguage(language);
        dbCourse.setLevel(level);
        dbCourse.setStatus(status.equalsIgnoreCase("published") ? "Active" : "Draft");
        dbCourse.setInstructorId(1); // placeholder admin id

        for (Category c : allCategories) {
            if (c.getName().equals(category)) {
                dbCourse.setCategoryId(c.getCategoryId());
                break;
            }
        }

        CourseDAO cDao = new CourseDAO();
        boolean success;
        if (newCourse || dbCourse.getCourseId() == 0) {
            success = cDao.insert(dbCourse);
            if (success) {
                newCourse = false;
                courseRow = new CoursesController.CourseRow(dbCourse.getCourseId(), title, category, "Admin", priceStr,
                        status, "Vừa xong");
            }
        } else {
            success = cDao.update(dbCourse);
        }

        if (success) {
            SectionDAO sDao = new SectionDAO();
            LessonDAO lDao = new LessonDAO();

            for (SectionWrapper sw : deletedSections) {
                if (sw.section.getSectionId() > 0)
                    sDao.delete(sw.section.getSectionId());
            }
            deletedSections.clear();

            for (LessonWrapper lw : deletedLessons) {
                if (lw.lesson.getLessonId() > 0)
                    lDao.delete(lw.lesson.getLessonId());
            }
            deletedLessons.clear();

            int secOrder = 1;
            for (SectionWrapper sw : contentSections) {
                sw.section.setCourseId(dbCourse.getCourseId());
                sw.section.setTitle(sw.title.get());
                sw.section.setOrderIndex(secOrder++);

                if (sw.section.getSectionId() == 0) {
                    sDao.insert(sw.section);
                } else {
                    sDao.update(sw.section);
                }

                int lesOrder = 1;
                for (LessonWrapper lw : sw.lessons) {
                    lw.lesson.setSectionId(sw.section.getSectionId());
                    lw.lesson.setTitle(lw.title.get());
                    lw.lesson.setContentType(lw.type.get());
                    try {
                        lw.lesson.setDurationSeconds(Integer.parseInt(lw.duration.get()) * 60);
                    } catch (Exception e) {
                        lw.lesson.setDurationSeconds(0);
                    }
                    lw.lesson.setPreview(lw.preview.get());
                    lw.lesson.setContent(lw.content.get());
                    lw.lesson.setOrderIndex(lesOrder++);

                    if (lw.lesson.getLessonId() == 0) {
                        lDao.insert(lw.lesson);
                    } else {
                        lDao.update(lw.lesson);
                    }
                }
            }
        }

        showAlert("Đã lưu", success ? "Đã cập nhật thông tin khóa học vào Database." : "Lỗi khi lưu vào Database.");
    }

    @FXML
    private void onPublishClicked() {
        if (readOnly) {
            showAlert("Chế độ xem", "Bạn đang ở chế độ xem chi tiết. Không thể xuất bản.");
            return;
        }
        if (courseRow == null)
            return;
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
        if (readOnly) {
            showAlert("Chế độ xem", "Bạn đang ở chế độ xem chi tiết. Không thể thêm Section.");
            return;
        }
        TextInputDialog d = new TextInputDialog("Section mới");
        d.setTitle("Thêm Section");
        d.setHeaderText("Nhập tên section");
        d.setContentText("Tên:");
        d.showAndWait().ifPresent(name -> {
            String t = name.trim();
            if (t.isEmpty())
                return;
            Section s = new Section();
            s.setTitle(t);
            SectionWrapper sw = new SectionWrapper(s);
            contentSections.add(sw);
            if (sectionsList != null)
                sectionsList.getSelectionModel().select(sw);
            updateLessonSummaryLabel();
        });
    }

    @FXML
    private void onRenameSectionClicked() {
        if (readOnly) {
            showAlert("Chế độ xem", "Chế độ xem chi tiết, không thể đổi tên.");
            return;
        }
        SectionWrapper sw = sectionsList == null ? null : sectionsList.getSelectionModel().getSelectedItem();
        if (sw == null) {
            showAlert("Thiếu lựa chọn", "Hãy chọn một section để đổi tên.");
            return;
        }
        TextInputDialog d = new TextInputDialog(sw.title.get());
        d.setTitle("Đổi tên Section");
        d.setHeaderText("Cập nhật tên section");
        d.setContentText("Tên:");
        d.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                sw.title.set(name.trim());
                sectionsList.refresh();
            }
        });
    }

    @FXML
    private void onDeleteSectionClicked() {
        if (readOnly) {
            showAlert("Chế độ xem", "Chế độ xem chi tiết, không thể xóa.");
            return;
        }
        SectionWrapper sw = sectionsList == null ? null : sectionsList.getSelectionModel().getSelectedItem();
        if (sw == null) {
            showAlert("Thiếu lựa chọn", "Hãy chọn một section để xóa.");
            return;
        }
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle("Xóa Section");
        a.setHeaderText("Xóa section \"" + sw.title.get() + "\"");
        a.setContentText("Tất cả bài giảng trong section này cũng sẽ bị xóa.");
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                deletedSections.add(sw);
                contentSections.remove(sw);
                updateLessonSummaryLabel();
                clearEditor();
            }
        });
    }

    @FXML
    private void onAddLessonClicked() {
        if (readOnly) {
            showAlert("Chế độ xem", "Chế độ xem chi tiết, không thể thêm bài giảng.");
            return;
        }
        SectionWrapper sw = sectionsList == null ? null : sectionsList.getSelectionModel().getSelectedItem();
        if (sw == null) {
            showAlert("Thiếu lựa chọn", "Hãy chọn một section trước khi thêm bài giảng.");
            return;
        }
        Dialog<LessonWrapper> dialog = new Dialog<>();
        dialog.setTitle("Thêm Bài giảng");
        dialog.setHeaderText("Tạo bài giảng mới");
        ButtonType createBtn = new ButtonType("Tạo", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        TextField title = new TextField();
        title.setPromptText("Tiêu đề bài giảng");
        ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList("video", "article", "quiz"));
        type.setValue("video");

        VBox box = new VBox(10, new Label("Tiêu đề *"), title, new Label("Loại"), type);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(bt -> {
            if (bt == createBtn) {
                String t = title.getText() == null ? "" : title.getText().trim();
                if (t.isEmpty())
                    return null;
                Lesson l = new Lesson();
                l.setTitle(t);
                l.setContentType(type.getValue());
                return new LessonWrapper(l);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(lw -> {
            sw.lessons.add(lw);
            lessonsList.getSelectionModel().select(lw);
            updateLessonSummaryLabel();
        });
    }

    @FXML
    private void onDeleteLessonClicked() {
        if (readOnly) {
            showAlert("Chế độ xem", "Chế độ xem chi tiết, không thể xóa bài giảng.");
            return;
        }
        LessonWrapper lw = lessonsList == null ? null : lessonsList.getSelectionModel().getSelectedItem();
        SectionWrapper sw = sectionsList == null ? null : sectionsList.getSelectionModel().getSelectedItem();
        if (sw == null || lw == null) {
            showAlert("Thiếu lựa chọn", "Hãy chọn một bài giảng để xóa.");
            return;
        }
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setTitle("Xóa Bài giảng");
        a.setHeaderText("Xóa bài giảng \"" + lw.title.get() + "\"");
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                deletedLessons.add(lw);
                sw.lessons.remove(lw);
                clearEditor();
                updateLessonSummaryLabel();
            }
        });
    }

    private void startEditingLesson(LessonWrapper lw) {
        editingLesson = lw;
        editorStateLabel.setText("Đang chỉnh: " + lw.title.get());
        lessonTitleField.setText(lw.title.get());
        lessonTypeField.setValue(lw.type.get());
        lessonDurationField.setText(lw.duration.get());
        lessonPreviewCheck.setSelected(lw.preview.get());

        lessonTitleField.setEditable(!readOnly);
        lessonTypeField.setDisable(readOnly);
        lessonDurationField.setEditable(!readOnly);
        lessonPreviewCheck.setDisable(readOnly);

        if (lessonHtmlEditor != null) {
            final String finalHtml;
            String html = lw.content.get();
            if (html == null || html.isBlank())
                finalHtml = "<p>" + escapeHtml(lw.title.get()) + "</p>";
            else
                finalHtml = html;
            Platform.runLater(() -> {
                try {
                    lessonHtmlEditor.setHtmlText(finalHtml);
                } catch (Exception ignore) {
                }
            });
        }
    }

    @FXML
    private void onSaveLessonClicked() {
        if (readOnly) {
            showAlert("Chế độ xem", "Chế độ xem chi tiết, không thể lưu bài giảng.");
            return;
        }
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
        editingLesson.type.set(lessonTypeField.getValue() == null ? "video" : lessonTypeField.getValue());
        editingLesson.duration.set(lessonDurationField.getText() == null ? "" : lessonDurationField.getText().trim());
        editingLesson.preview.set(lessonPreviewCheck.isSelected());
        if (lessonHtmlEditor != null) {
            editingLesson.content.set(lessonHtmlEditor.getHtmlText());
        }
        lessonsList.refresh();
        sectionsList.refresh();
        updateLessonSummaryLabel();
        showAlert("Đã lưu nháp", "Đã lưu nháp bài giảng. Hãy bấm 'Lưu thay đổi' để lưu vào CSDL.");
    }

    @FXML
    private void onCancelLessonEditClicked() {
        LessonWrapper lw = lessonsList == null ? null : lessonsList.getSelectionModel().getSelectedItem();
        if (lw != null)
            startEditingLesson(lw);
        else
            clearEditor();
    }

    @FXML
    private void onInsertImageClicked() {
        if (lessonHtmlEditor == null)
            return;
        TextInputDialog d = new TextInputDialog("https://");
        d.setTitle("Chèn ảnh");
        d.setHeaderText("Nhập URL ảnh để chèn vào nội dung bài giảng");
        d.showAndWait().ifPresent(url -> {
            String u = url.trim();
            if (u.isEmpty() || u.equals("https://"))
                return;
            lessonHtmlEditor.setHtmlText(lessonHtmlEditor.getHtmlText() + "<p><img src=\"" + escapeHtml(u)
                    + "\" style=\"max-width: 100%;\"/></p>");
        });
    }

    private static String escapeHtml(String s) {
        if (s == null)
            return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private void clearEditor() {
        editingLesson = null;
        if (editorStateLabel != null)
            editorStateLabel.setText("Chưa chọn bài giảng");
        if (lessonTitleField != null)
            lessonTitleField.clear();
        if (lessonTypeField != null)
            lessonTypeField.setValue("video");
        if (lessonDurationField != null)
            lessonDurationField.clear();
        if (lessonPreviewCheck != null)
            lessonPreviewCheck.setSelected(false);
        if (lessonHtmlEditor != null)
            Platform.runLater(() -> {
                try {
                    lessonHtmlEditor.setHtmlText("");
                } catch (Exception ignore) {
                }
            });
    }

    public static class EnrollmentRow {
        private final SimpleStringProperty student, date, progress;

        public EnrollmentRow(String student, String date, String progress) {
            this.student = new SimpleStringProperty(student);
            this.date = new SimpleStringProperty(date);
            this.progress = new SimpleStringProperty(progress);
        }

        public SimpleStringProperty studentProperty() {
            return student;
        }

        public SimpleStringProperty dateProperty() {
            return date;
        }

        public SimpleStringProperty progressProperty() {
            return progress;
        }
    }

    public static class ReviewRow {
        private final SimpleStringProperty user, rating, comment, date;

        public ReviewRow(String user, String rating, String comment, String date) {
            this.user = new SimpleStringProperty(user);
            this.rating = new SimpleStringProperty(rating);
            this.comment = new SimpleStringProperty(comment);
            this.date = new SimpleStringProperty(date);
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

        public SimpleStringProperty dateProperty() {
            return date;
        }
    }

    public static class SectionWrapper {
        public final Section section;
        public final SimpleStringProperty title = new SimpleStringProperty();
        public final ObservableList<LessonWrapper> lessons = FXCollections.observableArrayList();

        public SectionWrapper(Section section) {
            this.section = section;
            this.title.set(section.getTitle());
        }

        @Override
        public String toString() {
            return title.get();
        }
    }

    public static class LessonWrapper {
        public final Lesson lesson;
        public final SimpleStringProperty title = new SimpleStringProperty();
        public final SimpleStringProperty type = new SimpleStringProperty("video");
        public final SimpleStringProperty duration = new SimpleStringProperty("");
        public final SimpleBooleanProperty preview = new SimpleBooleanProperty(false);
        public final SimpleStringProperty content = new SimpleStringProperty("");

        public LessonWrapper(Lesson lesson) {
            this.lesson = lesson;
            this.title.set(lesson.getTitle());
            this.type.set(lesson.getContentType() != null ? lesson.getContentType() : "video");
            this.preview.set(lesson.isPreview());
            this.duration
                    .set(lesson.getDurationSeconds() != null ? String.valueOf(lesson.getDurationSeconds() / 60) : "");
            this.content.set(lesson.getContent() != null ? lesson.getContent() : "");
        }

        @Override
        public String toString() {
            return title.get();
        }
    }
}