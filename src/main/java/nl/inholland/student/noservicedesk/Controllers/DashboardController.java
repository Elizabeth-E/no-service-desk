package nl.inholland.student.noservicedesk.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;

import java.util.List;

public class DashboardController {

    @FXML
    private Label unresolvedIncidentsLabel;
    @FXML
    private Label incidentsPastDueLabel;
    private ServiceManager serviceManager;
    private MainViewController mainViewController;


    public DashboardController() {
    }

    public void buildDashboard() {
        List<Ticket> ticketList = serviceManager.getTicketService().getAllTickets();

        unresolvedIncidentsLabel.setText(serviceManager.getTicketService().getUnresolvedTicketCount() +"/" + ticketList.size());
        incidentsPastDueLabel.setText(serviceManager.getTicketService().getPastDeadlineCount() +"/" + ticketList.size());
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

    public void buildDashboardForEmployee(User user) {
        List<Ticket> ticketList = serviceManager.getTicketService().getAllTicketsForUser(user);

        unresolvedIncidentsLabel.setText(serviceManager.getTicketService().getUnresolvedTicketCount() +"/" + ticketList.size());
        incidentsPastDueLabel.setText(serviceManager.getTicketService().getPastDeadlineCount() +"/" + ticketList.size());
    }
}
