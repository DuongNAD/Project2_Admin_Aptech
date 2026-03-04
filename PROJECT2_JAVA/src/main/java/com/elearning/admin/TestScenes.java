package com.elearning.admin;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
public class TestScenes {
    public static void main(String[] args) {
        Platform.startup(() -> {
            String[] views = {"/views/enrollments_view.fxml", "/views/coupons_view.fxml", "/views/orders_view.fxml", "/views/reviews_view.fxml"};
            for (String v : views) {
                try {
                    FXMLLoader loader = new FXMLLoader(TestScenes.class.getResource(v));
                    loader.setCharset(java.nio.charset.StandardCharsets.UTF_8);
                    loader.load();
                    System.out.println("SUCCESS: " + v);
                } catch (Exception e) {
                    System.out.println("FAILED: " + v);
                    e.printStackTrace();
                }
            }
            Platform.exit();
        });
    }
}
