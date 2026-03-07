package com.elearning.admin.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class MainShellController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button coursesButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button ordersButton;

    @FXML
    private Button categoriesButton;

    @FXML
    private Button enrollmentsButton;

    @FXML
    private Button reviewsButton;

    @FXML
    private Button chatButton;

    @FXML
    public void initialize() {
        setActiveNav(dashboardButton);
        loadCenter("/views/dashboard_view.fxml");
    }

    @FXML
    private void onDashboardClicked() {
        setActiveNav(dashboardButton);
        loadCenter("/views/dashboard_view.fxml");
    }

    @FXML
    private void onCoursesClicked() {
        setActiveNav(coursesButton);
        loadCenter("/views/courses_view.fxml");
    }

    @FXML
    private void onUsersClicked() {
        setActiveNav(usersButton);
        loadCenter("/views/users_view.fxml");
    }

    @FXML
    private void onOrdersClicked() {
        setActiveNav(ordersButton);
        loadCenter("/views/orders_view.fxml");
    }

    @FXML
    private void onCategoriesClicked() {
        setActiveNav(categoriesButton);
        loadCenter("/views/categories_view.fxml");
    }

    @FXML
    private void onEnrollmentsClicked() {
        setActiveNav(enrollmentsButton);
        loadCenter("/views/enrollments_view.fxml");
    }

    @FXML
    private void onReviewsClicked() {
        setActiveNav(reviewsButton);
        loadCenter("/views/reviews_view.fxml");
    }

    @FXML
    private void onChatClicked() {
        setActiveNav(chatButton);
        loadCenter("/views/chat_view.fxml");
    }

    private void loadCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
            Node view = loader.load();

            // Setup fade transition
            view.setOpacity(0.0);
            rootPane.setCenter(view);

            FadeTransition fade = new FadeTransition(Duration.millis(300), view);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();

        } catch (Exception e) {
            System.err.println("Error loading ? FXML : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void setActiveNav(Button active) {
        Button[] buttons = {
                dashboardButton, coursesButton, usersButton, ordersButton,
                categoriesButton, enrollmentsButton,
                reviewsButton, chatButton
        };
        for (Button b : buttons) {
            if (b == null)
                continue;
            b.getStyleClass().remove("shell-nav-button-active");
        }
        if (!active.getStyleClass().contains("shell-nav-button-active")) {
            active.getStyleClass().add("shell-nav-button-active");
        }
    }
}
