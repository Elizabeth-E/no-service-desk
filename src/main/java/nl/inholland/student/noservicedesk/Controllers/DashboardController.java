package nl.inholland.student.noservicedesk.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.TicketService;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML
    private Label unresolvedIncidentsLabel;
    @FXML
    private Label incidentsPastDueLabel;
    private ServiceManager serviceManager;
    private Stage stage;
    private TicketService ticketService;
    private MainViewController mainViewController;


    public DashboardController() {
    }

    public void buildDashboard() {
        ticketService = serviceManager.getTicketService();

        List<Ticket> ticketList = ticketService.getAllTickets();

        System.out.println("Dashboard loaded.");

        unresolvedIncidentsLabel.setText(ticketService.getUnresolvedTicketCount() +"/" + ticketList.size());

        incidentsPastDueLabel.setText(ticketService.getPastDeadlineCount() +"/" + ticketList.size());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @FXML
    public void onShowListButton(){
        mainViewController.showTickets();
    }
}
