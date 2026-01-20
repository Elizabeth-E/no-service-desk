package nl.inholland.student.noservicedesk.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;

import java.util.List;
import java.util.Optional;

import static nl.inholland.student.noservicedesk.Controllers.AlertHelper.showAlert;

public class UserManagementController {

    public BorderPane viewLayout;
    private ServiceManager serviceManager;
    private MainViewController mainViewController;
    private List<User> users;

    @FXML private TableView<User> userTableview;
    @FXML private TableColumn<User, String> idColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> emailAddressColumn;
    @FXML private TableColumn<User, String> locationColumn;
    @FXML private TableColumn<User, String> phoneColumn;

    public void buildUsersTableView() {
        // makes userid object a string
        idColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().get_id() == null ? "" : cd.getValue().get_id().toString()
                )
        );

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        emailAddressColumn.setCellValueFactory(new PropertyValueFactory<>("email_address"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        users = serviceManager.getUserService().getAllUsers();

        ObservableList<User> userOverview = FXCollections.observableArrayList(users);
        userTableview.setItems(userOverview);
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void onDeleteUserButtonClick() {
        User user = userTableview.getSelectionModel().getSelectedItem();
        if (user == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No ticket selected",
                    "Nothing to delete",
                    "Please select a ticket first."
            );
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm deletion");
        confirmation.setHeaderText("Delete User");
        confirmation.setContentText(
                "Are you sure you want to delete this ticket?\n\n" +
                        "Name: " + user.getFullName() + "\n\n" +
                        "Email: " + user.getEmail_address()
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceManager.getUserService().deleteUser(user.get_id());

                //refresh table after delete
                userTableview.getItems().remove(user);

            } catch (JsonProcessingException e) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Error",
                        "Could not delete user",
                        "Something went wrong while removing the user.\n" + e.getMessage()
                );
            }
        }
    }

    public void onUpdateUserButtonClick() {
        mainViewController.showUpdateUser(userTableview.getSelectionModel().getSelectedItem());
    }

    public void onAddUserButtonClick() {
        mainViewController.showCreateNewUser();
    }
}
