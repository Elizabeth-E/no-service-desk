package nl.inholland.student.noservicedesk.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.TicketService;

import java.util.List;
import java.util.Optional;

import static nl.inholland.student.noservicedesk.Controllers.AlertHelper.showAlert;

public class TicketsOverviewController {
    public BorderPane viewLayout;
    private ServiceManager serviceManager;
    private TicketService ticketService;
    private MainViewController mainViewController;
    private List<Ticket> tickets;

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
        ticketService = serviceManager.getTicketService();
    }

    public void fillTicketsTable(){

        buildTicketsTableView();

        ObservableList<Ticket> ticketsOverview = FXCollections.observableArrayList(tickets);
        ticketsTableview.setItems(ticketsOverview);
    }

    public void fillUserTicketsTableView(User user){
        tickets = serviceManager.getTicketService().getAllTicketsForUser(user);
        fillTicketsTable();
    }

    public void fillServiceDeskEmployeeTicketsTableView(){
        tickets = serviceManager.getTicketService().getAllTickets();
        fillTicketsTable();
    }

    public void buildTicketsTableView(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("_id"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("reported_by_name"));
        dateReportedColumn.setCellValueFactory(new PropertyValueFactory<>("date_created"));
        dueColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        isResolvedColumn.setCellValueFactory(new PropertyValueFactory<>("is_resolved"));
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void onCreateIncidentButtonClick() {
        mainViewController.showCreateIncident();
    }

    public void onDeleteTicketButtonClick(ActionEvent event) {
        Ticket deleteTicket = ticketsTableview.getSelectionModel().getSelectedItem();

        if (deleteTicket == null) {
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
        confirmation.setHeaderText("Delete ticket");
        confirmation.setContentText(
                "Are you sure you want to delete this ticket?\n\n" +
                        "Subject: " + deleteTicket.getSubject() + "\n\n" +
                        "Description: " + deleteTicket.getDescription() + "\n\n" +
                        "Reported by: " + deleteTicket.getReported_by_name()
        );

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ticketService.deleteTicket(deleteTicket);

                // Optional: refresh table after delete
                ticketsTableview.getItems().remove(deleteTicket);

            } catch (JsonProcessingException e) {
                showAlert(
                        Alert.AlertType.ERROR,
                        "Error",
                        "Could not delete ticket",
                        "Something went wrong while removing the ticket.\n" + e.getMessage()
                );
            }
        }
    }


    public void onUpdateTicketButtonClick(ActionEvent event) {
        Ticket updateTicket = ticketsTableview.getSelectionModel().getSelectedItem();;
        mainViewController.showUpdateTicket(updateTicket);
    }

    public void onViewTicketHistoryButtonClick(ActionEvent event) {
        Ticket ticketHistory = ticketsTableview.getSelectionModel().getSelectedItem();;
        mainViewController.showTicketHistory(ticketHistory);
    }
}
