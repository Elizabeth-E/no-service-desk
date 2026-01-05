package nl.inholland.student.noservicedesk.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Models.Priority;
import nl.inholland.student.noservicedesk.Models.Subject;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.TicketService;

import java.time.LocalDateTime;
import java.util.List;

public class TicketsOverviewController {
    public BorderPane viewLayout;
    private ServiceManager serviceManager;
    private Stage stage;
    private TicketService ticketService;
    private MainViewController mainViewController;

    @FXML
    private TableView<Ticket> ticketsTableview;
    @FXML
    private TableColumn<Ticket, String> idColumn;
    @FXML
    private TableColumn<Ticket, String> subjectColumn;
    @FXML
    private TableColumn<Ticket, String> userColumn;
    @FXML
    private TableColumn<Ticket, String> dateReportedColumn;
    @FXML
    private TableColumn<Ticket, String> dueColumn;
    @FXML
    private TableColumn<Ticket, Boolean> isResolvedColumn;


    public TicketsOverviewController() {
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void fillTicketsTable(){

        idColumn.setCellValueFactory(new PropertyValueFactory<>("_id"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("reported_by"));
        dateReportedColumn.setCellValueFactory(new PropertyValueFactory<>("date_created"));
        dueColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        isResolvedColumn.setCellValueFactory(new PropertyValueFactory<>("is_resolved"));

        ticketService = serviceManager.getTicketService();
        List<Ticket> tickets = ticketService.getAllTickets();

        ObservableList<Ticket> ticketsOverview = FXCollections.observableArrayList(tickets);

        ticketsTableview.setItems(ticketsOverview);
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void onCreateIncidentButtonClick() {
        mainViewController.showCreateIncident();
    }

}
