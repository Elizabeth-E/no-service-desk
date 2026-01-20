package nl.inholland.student.noservicedesk.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import nl.inholland.student.noservicedesk.Models.Priority;
import nl.inholland.student.noservicedesk.Models.Subject;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.TicketService;
import nl.inholland.student.noservicedesk.services.UserService;

import java.time.Instant;
import java.util.List;

import static nl.inholland.student.noservicedesk.Controllers.AlertHelper.showAlert;

public class CreateNewTicketController {

    @FXML private ComboBox<Subject> ticketSubjects;
    @FXML private ComboBox<Priority> ticketPriorities;
    @FXML private ComboBox<String> followUpDeadline;
    @FXML private TextArea ticketDescription;
    @FXML private ComboBox<String> reportedByUsers;
    @FXML private TextField reporterEmail;
    @FXML public BorderPane viewLayout;

    private ServiceManager serviceManager;
    private MainViewController mainViewController;

    public void setServiceManager(ServiceManager serviceManager) {this.serviceManager = serviceManager;}
    public void setMainViewController(MainViewController mainViewController) {this.mainViewController = mainViewController;}

    public void buildCreateNewTicketForm() {
        ticketSubjects.getItems().setAll(Subject.values());
        ticketPriorities.getItems().setAll(Priority.values());

        for (int i = 1; i <= 7; i++) {
            followUpDeadline.getItems().add(String.valueOf(i));
        }

        List<User> allUsers = this.serviceManager.getUserService().getAllUsers();
        List<String> usernames = allUsers.stream()
                .map(User::getFullName)
                .toList();

        reportedByUsers.getItems().setAll(usernames);
    }

    public void onSubmitTicketButtonClick() {
        TicketService ticketService = serviceManager.getTicketService();
        UserService userService = serviceManager.getUserService();
        Ticket ticket = new Ticket();

        if (ticketSubjects.getValue() != null)
            ticket.setSubject(ticketSubjects.getValue().toString());

        if (ticketPriorities.getValue() != null)
            ticket.setPriority(ticketPriorities.getValue().toString());

        if (followUpDeadline.getValue() != null)
            ticketService.setDeadlineFromCreatedAndDays(ticket, followUpDeadline.getValue());

        if (ticketDescription.getText() != null)
            ticket.setDescription(ticketDescription.getText());

        if (reportedByUsers.getValue() != null || reporterEmail.getText() != null)
            try{
                if(userService.getUserByEmail(reporterEmail.getText()) == null){
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not find user",
                            "Something went wrong while finding the user.");
                }

                User user = userService.getUserByEmail(reporterEmail.getText());
                ticket.setReported_by(user.get_id());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        ticket.setDate_created(Instant.now());
        ticket.setStatus("Open");
        ticket.setIs_resolved(false);

        try {
            ticketService.setDeadlineFromCreatedAndDays(ticket, followUpDeadline.getValue());
            ticketService.createTicket(ticket);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket added successfully",
                    "Ticket was created successfully");
            clearTicketsInput();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not submit ticket",
                    "Something went wrong while creating the ticket.");
        }

    }

    public void clearTicketsInput() {
        ticketSubjects.getSelectionModel().clearSelection();
        ticketPriorities.getSelectionModel().clearSelection();
        followUpDeadline.getSelectionModel().clearSelection();
        reportedByUsers.getSelectionModel().clearSelection();
        ticketDescription.clear();
    }

    public void onCancelNewTicketButtonClick() {
        mainViewController.showTickets();
    }
}
