package nl.inholland.student.noservicedesk.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.UserService;

import java.util.List;

public class UserManagementController {

    public BorderPane viewLayout;
    private ServiceManager serviceManager;
    private MainViewController mainViewController;
    private List<User> users;

    @FXML
    private TableView<User> userTableview;
    @FXML
    private TableColumn<User, String> idColumn;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableColumn<User, String> emailAddressColumn;
    @FXML
    private TableColumn<User, String> locationColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;

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
}
