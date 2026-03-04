package com.elearning.admin.controllers;

import com.elearning.admin.dao.AdminDAO;
import com.elearning.admin.models.Admin;
import com.elearning.admin.utils.PasswordUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginViewController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheck;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    private final AdminDAO adminDAO = new AdminDAO();

    @FXML
    private void onLoginClicked() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            showError("Vui lòng nhập đầy đủ email và mật khẩu.");
            return;
        }

        try {
            Admin admin = adminDAO.getByEmail(email);
            if (admin != null) {
                boolean isPasswordMatch = PasswordUtil.checkPassword(password, admin.getPasswordHash());
                if (isPasswordMatch) {
                    hideError();
                    System.out.println("Login successful for ? Admin : " + admin.getFullName());
                    switchToMainShell();
                } else {
                    showError("Sai mật khẩu. Vui lòng thử lại.");
                }
            } else {
                showError("Email không tồn tại trong hệ thống.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi kết nối máy chủ hoặc lỗi hệ thống.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void switchToMainShell() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main_shell.fxml"));
            loader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 720);
            scene.getStylesheets().addAll(
                    getClass().getResource("/css/main_styles.css").toExternalForm(),
                    getClass().getResource("/css/dashboard_styles.css").toExternalForm());

            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Không thể mở màn hình chính.");
        }
    }
}
