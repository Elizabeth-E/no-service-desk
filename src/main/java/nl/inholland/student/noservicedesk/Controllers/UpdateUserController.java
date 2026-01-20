package nl.inholland.student.noservicedesk.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import nl.inholland.student.noservicedesk.Models.Location;
import nl.inholland.student.noservicedesk.Models.Role;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;

import java.util.Optional;

import static nl.inholland.student.noservicedesk.Controllers.AlertHelper.showAlert;

public class UpdateUserController {

    private ServiceManager serviceManager;
    private MainViewController mainViewController;
    private User selectedUser;

    @FXML public BorderPane viewLayout;
    @FXML private TextField newUserFirstName;
    @FXML private TextField newUserLastName;
    @FXML private ComboBox<Role> userRoleComboBox;
    @FXML private TextField newUserEmail;
    @FXML private TextField newUserPhoneNumber;
    @FXML private ComboBox<Location> userLocationComboBox;

    @FXML
    private void initialize() {
        // Fill comboboxes
        userRoleComboBox.getItems().setAll(Role.values());
        userLocationComboBox.getItems().setAll(Location.values());

        // Bind UI
        newUserFirstName.textProperty().addListener((obs, oldV, newV) -> {
            if (selectedUser != null) selectedUser.setFirst_name(newV);
        });

        newUserLastName.textProperty().addListener((obs, oldV, newV) -> {
            if (selectedUser != null) selectedUser.setLast_name(newV);
        });

        newUserEmail.textProperty().addListener((obs, oldV, newV) -> {
            if (selectedUser != null) selectedUser.setEmail_address(newV);
        });

        newUserPhoneNumber.textProperty().addListener((obs, oldV, newV) -> {
            if (selectedUser != null) selectedUser.setPhone(newV);
        });

        userRoleComboBox.valueProperty().addListener((obs, oldV, newV) -> {
            if (selectedUser != null) selectedUser.setRole(newV.name());
        });

        userLocationComboBox.valueProperty().addListener((obs, oldV, newV) -> {
            if (selectedUser != null) selectedUser.setLocation(newV.name());
        });
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void buildUpdateUserForm(User user) {
        this.selectedUser = user;

        newUserFirstName.setText(Optional.ofNullable(user.getFirst_name()).orElse(""));
        newUserLastName.setText(Optional.ofNullable(user.getLast_name()).orElse(""));
        newUserEmail.setText(Optional.ofNullable(user.getEmail_address()).orElse(""));
        newUserPhoneNumber.setText(Optional.ofNullable(user.getPhone()).orElse(""));

        if (user.getRole() != null) {
            userRoleComboBox.getItems().stream()
                    .filter(r -> r.toString().equals(user.getRole()))
                    .findFirst()
                    .ifPresent(userRoleComboBox::setValue);
        } else {
            userRoleComboBox.setValue(null);
        }

        if (user.getLocation() != null) {
            userLocationComboBox.getItems().stream()
                    .filter(l -> l.toString().equals(user.getLocation()))
                    .findFirst()
                    .ifPresent(userLocationComboBox::setValue);
        } else {
            userLocationComboBox.setValue(null);
        }
    }

    public void onUpdateUserButtonClick() {
        if (selectedUser == null) return;

        try {
            serviceManager.getUserService().updateUser(selectedUser);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "User Updated",
                    "User updated.",
                    "User successfully updated."
            );

            mainViewController.showUserManagement();

        } catch (JsonProcessingException e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Error updating user",
                    "User could not be updated.",
                    "Failed to update user. " + e.getMessage()
            );
        } catch (Exception e) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Error updating user",
                    "User could not be updated.",
                    "Something went wrong. " + e.getMessage()
            );
        }
    }

    public void onCancelButtonClick() {
        mainViewController.showUserManagement();
    }
}
