package nl.inholland.student.noservicedesk.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private ServiceManager serviceManager;
    private Stage stage;
    private TicketService ticketService;
    private MainViewController mainViewController;


    @FXML
    private ComboBox<Subject> ticketSubjects;
    @FXML
    private ComboBox<Priority> ticketPriorities;
    @FXML
    private ComboBox<String> followUpDeadline;
    @FXML
    private TextArea ticketDescription;
    @FXML
    private ComboBox<String> reportedByUsers;
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

    public void buildForm() {
        ticketSubjects.getItems().setAll(Subject.values());
        ticketPriorities.getItems().setAll(Priority.values());

        for (int i = 1; i <= 7; i++) {
            followUpDeadline.getItems().add(i + "");
        }

        List<User> allUsers = this.serviceManager.getUserService().getAllUsers();
        List<String> usernames = allUsers.stream()
                .map(User::getFullName)
                .toList();

        reportedByUsers.getItems().setAll(usernames);
    }

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

    public void onCreateIncidentButtonClick(ActionEvent event) {
        mainViewController.loadCreateIncident();
    }

    public void onSubmitTicketButtonClick(ActionEvent event) {
        ticketService = serviceManager.getTicketService();
        Ticket ticket = new Ticket();

        if (ticketSubjects.getValue() != null)
            ticket.setSubject(ticketSubjects.getValue().toString());

        if (ticketPriorities.getValue() != null)
            ticket.setPriority(ticketPriorities.getValue().toString());

        if (followUpDeadline.getValue() != null)
            ticket.setDeadline(followUpDeadline.getValue());

        if (ticketDescription.getText() != null)
            ticket.setDescription(ticketDescription.getText());

        if (reportedByUsers.getValue() != null)
            ticket.setReported_by(reportedByUsers.getValue());

        ticket.setDate_created(LocalDateTime.now().toString());
        ticket.setStatus("Open");
        ticket.setIs_resolved(false);

        try {
            ticketService.createTicket(ticket);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ticket Created");
            alert.setHeaderText("Ticket Created");
            alert.setContentText("Ticket Created Successfully");
            alert.showAndWait();
            clearTicketsInput();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearTicketsInput() {
        ticketSubjects.getSelectionModel().clearSelection();
        ticketPriorities.getSelectionModel().clearSelection();
        followUpDeadline.getSelectionModel().clearSelection();
        reportedByUsers.getSelectionModel().clearSelection();
        ticketDescription.clear();
    }
}
