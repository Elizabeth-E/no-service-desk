package nl.inholland.student.noservicedesk.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.NoServiceDeskApplication;
import nl.inholland.student.noservicedesk.services.ServiceManager;

import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;

import static nl.inholland.student.noservicedesk.Controllers.AlertHelper.showAlert;


public class MainViewController {
    @FXML private BorderPane root;
    @FXML private MenuItem createUserMenu;
    @FXML private MenuItem userManagement;
    @FXML private MenuBar menu;

    private Stage stage;
    private ServiceManager serviceManager;
    private  User user;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @FXML
    public void showDashboard() {
        if(isServiceDeskEmployee(user)) {
            showDashboardForServiceDeskEmployee();
        }
        else {
            showUserDashboard(user);
        }
    }

    @FXML
    public void showDashboardForServiceDeskEmployee() {
        setCenter("Dashboard-view.fxml", controller -> {
            if (controller instanceof DashboardController dc) {
                dc.setServiceManager(serviceManager);
                dc.setMainViewController(this);
                dc.buildDashboard();
            }
        });
        if (stage != null) stage.setTitle("Dashboard");
    }

    @FXML
    public void showTickets() {
        setCenter("TicketsOverview-view.fxml", controller -> {
            if (controller instanceof TicketsOverviewController tc) {
                tc.setServiceManager(serviceManager);
                tc.setMainViewController(this);
                if(isServiceDeskEmployee(user)) {
                    tc.fillServiceDeskEmployeeTicketsTableView();
                }
                else {
                    tc.fillUserTicketsTableView(user);
                }
            }
        });
        if (stage != null) stage.setTitle("Tickets Overview");
    }

    @FXML
    public void showCreateIncident() {
        setCenter("CreateNewTicket-view.fxml", controller -> {
            if (controller instanceof CreateNewTicketController cc) {
                cc.setServiceManager(serviceManager);
                cc.setMainViewController(this);
                cc.buildCreateNewTicketForm();
            }
        });
        if (stage != null) stage.setTitle("Create Ticket");
    }

    @FXML
    public void showCreateNewUser() {
        setCenter("CreateNewUser-view.fxml", controller -> {
            if (controller instanceof CreateNewUserController cc) {
                cc.setServiceManager(serviceManager);
                cc.setMainViewController(this);
                cc.buildCreateNewUserForm();
            }
        });
        if (stage != null) stage.setTitle("Create Ticket");
    }
    @FXML
    public void showUpdateUser(User user) {
        setCenter("UpdateUser-view.fxml", controller -> {
            if (controller instanceof UpdateUserController usc) {
                usc.setServiceManager(serviceManager);
                usc.setMainViewController(this);
                usc.buildUpdateUserForm(user);
            }
        });
        if (stage != null) stage.setTitle("Create Ticket");
    }
    @FXML
    public void showUpdateTicket(Ticket ticket) {
        setCenter("UpdateTicket-view.fxml", controller -> {
            if (controller instanceof UpdateTicketController utc) {
                utc.setServiceManager(serviceManager);
                utc.setMainViewController(this);
                utc.setCurrentUser(user);
                utc.buildUpdateTicketForm(ticket);
            }
        });
        if (stage != null) stage.setTitle("Create Ticket");
    }

    private void setCenter(String fxml, java.util.function.Consumer<Object> afterLoad) {
        try {
            FXMLLoader loader = new FXMLLoader(NoServiceDeskApplication.class.getResource(fxml));
            Parent view = loader.load();
            Object controller = loader.getController();

            afterLoad.accept(controller);
            root.setCenter(view);

        } catch (Exception e) {
            showAlert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Error",
                    "Could not load view",
                    "Something went wrong while loading view.\n" + e.getMessage()
            );
        }
    }

    public void showUserDashboard(User user) {
        Menu navigateMenu = menu.getMenus().getFirst();
        navigateMenu.getItems().remove(createUserMenu);
        navigateMenu.getItems().remove(userManagement);

        setCenter("Dashboard-view.fxml", controller -> {
            if (controller instanceof DashboardController dc) {
                dc.setServiceManager(serviceManager);
                dc.setMainViewController(this);
                dc.buildDashboardForEmployee(user);
            }
        });
    }

    public void showUserManagement() {
        setCenter("UserManagement-view.fxml", controller -> {
            if (controller instanceof UserManagementController umc) {
                umc.setServiceManager(serviceManager);
                umc.setMainViewController(this);
                umc.buildUsersTableView();
            }
        });
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean isServiceDeskEmployee(User user) {
        if(user.getRole().equals("SERVICEDESKEMPLOYEE") || user.getRole().equals("ADMIN")) {
            return true;
        }
        else if(user.getRole().equals("EMPLOYEE")) {
            return false;
        }
        return false;
    }

    public void showTicketHistory(Ticket ticketHistory) {
        setCenter("TicketHistory-view.fxml", controller -> {
            if (controller instanceof HandledTicketHistoryController hc) {
                hc.setServiceManager(serviceManager);
                hc.setMainViewController(this);
                hc.setTicket(ticketHistory);
                hc.buildHistoryTableView();
            }
        });
    }
}
