package com.elearning.admin.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import com.elearning.admin.dao.ChatDAO;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatController {

    @FXML
    private TextField searchField;
    @FXML
    private Label totalLabel, countLabel;
    @FXML
    private VBox messagesContainer;
    @FXML
    private Button deleteSelectedBtn;
    @FXML
    private Button clearAllBtn;

    private ObservableList<ChatDAO.ChatMessageDTO> allMessages = FXCollections.observableArrayList();
    private FilteredList<ChatDAO.ChatMessageDTO> filteredMessages;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

    @FXML
    public void initialize() {
        loadData();
        setupSearch();
    }

    private void loadData() {
        ChatDAO dao = new ChatDAO();
        List<ChatDAO.ChatMessageDTO> list = dao.getAll();
        allMessages.setAll(list);
        filteredMessages = new FilteredList<>(allMessages, p -> true);
        totalLabel.setText("Tổng: " + dao.getTotalCount() + " tin nhắn");
        countLabel.setText(filteredMessages.size() + " tin nhắn");
        renderMessages();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((o, a, b) -> applySearch());
    }

    private void applySearch() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        if (keyword.isEmpty()) {
            filteredMessages.setPredicate(p -> true);
        } else {
            filteredMessages.setPredicate(p -> {
                String msg = p.message == null ? "" : p.message.toLowerCase();
                String user = p.userName == null ? "" : p.userName.toLowerCase();
                return msg.contains(keyword) || user.contains(keyword);
            });
        }
        countLabel.setText(filteredMessages.size() + " tin nhắn");
        renderMessages();
    }

    private void renderMessages() {
        messagesContainer.getChildren().clear();
        if (filteredMessages.isEmpty()) {
            Label empty = new Label("Không có tin nhắn nào.");
            empty.getStyleClass().add("chat-empty-label");
            messagesContainer.getChildren().add(empty);
            return;
        }
        for (ChatDAO.ChatMessageDTO dto : filteredMessages) {
            messagesContainer.getChildren().add(buildMsgCard(dto));
        }
    }

    private HBox buildMsgCard(ChatDAO.ChatMessageDTO dto) {
        HBox card = new HBox(14);
        card.getStyleClass().add("chat-msg-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 16, 12, 16));

        // Avatar placeholder
        Label avatar = new Label(dto.userName.isEmpty() ? "?" : dto.userName.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("chat-avatar");
        avatar.setMinSize(40, 40);
        avatar.setMaxSize(40, 40);
        avatar.setAlignment(Pos.CENTER);

        // Content
        VBox content = new VBox(4);
        HBox.setHgrow(content, Priority.ALWAYS);

        HBox meta = new HBox(10);
        meta.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(dto.userName);
        nameLabel.getStyleClass().add("chat-msg-user");
        Label dateLabel = new Label(dto.createdAt != null ? sdf.format(dto.createdAt) : "N/A");
        dateLabel.getStyleClass().add("chat-msg-date");
        Label idLabel = new Label("#" + dto.msgId);
        idLabel.getStyleClass().add("chat-msg-id");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        meta.getChildren().addAll(nameLabel, idLabel, spacer, dateLabel);

        Label msgLabel = new Label(dto.message);
        msgLabel.setWrapText(true);
        msgLabel.getStyleClass().add("chat-msg-content");
        msgLabel.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().addAll(meta, msgLabel);

        // Delete button
        Button deleteBtn = new Button("Xóa");
        deleteBtn.getStyleClass().add("chat-btn-delete");
        deleteBtn.setTooltip(new Tooltip("Xóa tin nhắn này"));
        deleteBtn.setOnAction(e -> deleteMessage(dto));

        card.getChildren().addAll(avatar, content, deleteBtn);
        return card;
    }

    private void deleteMessage(ChatDAO.ChatMessageDTO dto) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Xóa tin nhắn");
        a.setHeaderText("Bạn có chắc muốn xóa tin nhắn này?");
        a.setContentText("\"" + dto.message + "\"");
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                ChatDAO dao = new ChatDAO();
                if (dao.delete(dto.msgId)) {
                    allMessages.remove(dto);
                    totalLabel.setText("Tổng: " + allMessages.size() + " tin nhắn");
                    countLabel.setText(filteredMessages.size() + " tin nhắn");
                    renderMessages();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Không thể xóa tin nhắn.").showAndWait();
                }
            }
        });
    }

    @FXML
    private void onSearchCleared() {
        searchField.clear();
    }

    @FXML
    private void onRefreshClicked() {
        searchField.clear();
        loadData();
    }

    @FXML
    private void onDeleteAllViolatingClicked() {
        if (filteredMessages.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Không có tin nhắn nào để xóa.").showAndWait();
            return;
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Xóa tất cả kết quả lọc");
        a.setHeaderText("Xóa " + filteredMessages.size() + " tin nhắn?");
        a.setContentText("Tất cả các tin nhắn đang hiển thị sẽ bị xóa vĩnh viễn.");
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                ChatDAO dao = new ChatDAO();
                List<ChatDAO.ChatMessageDTO> toDelete = List.copyOf(filteredMessages);
                int deleted = 0;
                for (ChatDAO.ChatMessageDTO dto : toDelete) {
                    if (dao.delete(dto.msgId)) {
                        allMessages.remove(dto);
                        deleted++;
                    }
                }
                totalLabel.setText("Tổng: " + allMessages.size() + " tin nhắn");
                countLabel.setText(filteredMessages.size() + " tin nhắn");
                renderMessages();
                new Alert(Alert.AlertType.INFORMATION, "Đã xóa " + deleted + " tin nhắn.").showAndWait();
            }
        });
    }
}
