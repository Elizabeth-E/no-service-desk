package nl.inholland.student.noservicedesk.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.NoServiceDeskApplication;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.UserService;

public class LoginController {

    @FXML private TextField emailTextField;
    @FXML private TextField passwordTextField;
    @FXML private Label errorLabel;

    private ServiceManager serviceManager;
    private Stage stage;

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @FXML
    public void onLoginButtonClick(ActionEvent event) {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        UserService userService = serviceManager.getUserService();

        try {
            if (userService.authenticate(email, password)) {
                User loginUser = userService.getUserByEmail(email);

                FXMLLoader loader = new FXMLLoader(NoServiceDeskApplication.class.getResource("Main-view.fxml"));
                Parent mainRoot = loader.load();

                MainViewController main = loader.getController();

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                main.setStage(stage);
                main.setServiceManager(serviceManager);

                stage.setScene(new Scene(mainRoot, 800, 600));
                stage.setTitle("Dashboard");
                stage.show();

                main.setUser(loginUser);
                main.showDashboard();

            } else {
                errorLabel.setText("Wrong username or password!");
            }
        } catch (Exception e) {
            errorLabel.setText("Login failed: " + e.getMessage());
        }
    }

    public void onLoginCancelButtonClick() {
        stage.close();
    }
}
