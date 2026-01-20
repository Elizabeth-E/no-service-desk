package nl.inholland.student.noservicedesk.Controllers;

import javafx.scene.control.Alert;

public class AlertHelper {
    static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
