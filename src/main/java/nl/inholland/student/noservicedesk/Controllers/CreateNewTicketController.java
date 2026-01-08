package nl.inholland.student.noservicedesk.Controllers;

import javafx.event.ActionEvent;
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
import java.time.LocalDateTime;
import java.util.List;

public class CreateNewTicketController {

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
    private TextField reporterEmail;
    @FXML
    public BorderPane viewLayout;

    private ServiceManager serviceManager;
    private TicketService ticketService;
    private UserService userService;
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

    public void onSubmitTicketButtonClick(ActionEvent event) {
        ticketService = serviceManager.getTicketService();
        userService = serviceManager.getUserService();
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
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText("User not found");
                    alert.showAndWait();
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
