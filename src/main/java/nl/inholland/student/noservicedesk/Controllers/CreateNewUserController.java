package nl.inholland.student.noservicedesk.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Models.Location;
import nl.inholland.student.noservicedesk.Models.Role;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.UserService;
import java.util.regex.Pattern;

import static nl.inholland.student.noservicedesk.Controllers.AlertHelper.showAlert;

public class CreateNewUserController {

    @FXML private TextField newUserFirstName;
    @FXML private TextField newUserLastName;
    @FXML private ComboBox<Role> userRoleComboBox;
    @FXML private TextField newUserEmail;
    @FXML private TextField newUserPhoneNumber;
    @FXML private ComboBox<Location> userLocationComboBox;
    @FXML private CheckBox sendPasswordCheckBox;
    @FXML public BorderPane viewLayout;
    @FXML public Button cancelButton;

    private ServiceManager serviceManager;
    private UserService userService;
    private MainViewController mainViewController;

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void buildCreateNewUserForm() {
        userService = serviceManager.getUserService();

        userRoleComboBox.getItems().setAll(Role.values());

        if (userLocationComboBox != null) {
            userLocationComboBox.getItems().setAll(Location.values());
        }
    }

    public void onAddUserButtonClick(ActionEvent event) {
        userService = serviceManager.getUserService();

        String firstName = newUserFirstName.getText();
        String lastName = newUserLastName.getText();
        Role role = userRoleComboBox.getValue();
        String email = newUserEmail.getText();
        String phone = newUserPhoneNumber.getText();

        Location location = null;
        if (userLocationComboBox != null) {
            location = userLocationComboBox.getValue();
        }

        // Required fields
        if (firstName.isEmpty() || lastName.isEmpty() || role == null || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Missing required fields",
                    "First name, last name, role and email are required.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid email",
                    "Please enter a valid email address.");
            return;
        }

        try {
            // Duplicate check
            if (userService.getUserByEmail(email) != null) {
                showAlert(Alert.AlertType.ERROR, "Error", "User already exists",
                        "A user with this email address already exists.");
                return;
            }

            User user = new User();
            user.setFirst_name(firstName);
            user.setLast_name(lastName);
            user.setRole(role.toString());
            user.setEmail_address(email);
            user.setPhone(phone);
            user.setLocation(location.toString());

            // if send password not ticked make temp
            if (sendPasswordCheckBox != null && sendPasswordCheckBox.isSelected()) {
                user.setPassword(generateTempPassword(10));
                //an email would then be sent to user to share the generated password
            } else {
                //this is a place holder for the sake of the application
                user.setPassword("Password123");
            }

            userService.createNewUser(user);

            showAlert(Alert.AlertType.INFORMATION, "User Created", "User Created",
                    "User created successfully.");
            clearUserInput();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not create user",
                    "Something went wrong while creating the user.");
        }
    }

    public void onCancelButtonClick(ActionEvent event) {
        mainViewController.showTickets();
    }

    private void clearUserInput() {
        newUserFirstName.clear();
        newUserLastName.clear();
        userRoleComboBox.getSelectionModel().clearSelection();
        newUserEmail.clear();
        newUserPhoneNumber.clear();

        if (userLocationComboBox != null) {
            userLocationComboBox.getSelectionModel().clearSelection();
        }
        if (sendPasswordCheckBox != null) {
            sendPasswordCheckBox.setSelected(false);
        }
    }

    private static boolean isValidEmail(String email) {
        Pattern p = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        return p.matcher(email).matches();
    }

    private static String generateTempPassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }


}
