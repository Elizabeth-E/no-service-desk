package nl.inholland.student.noservicedesk.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Models.*;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import nl.inholland.student.noservicedesk.services.TicketService;
import nl.inholland.student.noservicedesk.services.UserService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static nl.inholland.student.noservicedesk.Controllers.AlertHelper.showAlert;

public class UpdateTicketController {
    private ServiceManager serviceManager;
    private TicketService ticketService;
    private UserService userService;
    private MainViewController mainViewController;

    private Ticket currentTicket;
    private List<User> cachedUsers;
    private User currentUser;

    @FXML private ComboBox<Subject> ticketSubjects;
    @FXML private ComboBox<Priority> ticketPriorities;
    @FXML private ComboBox<String> followUpDeadline;
    @FXML private TextArea ticketDescription;
    @FXML private ComboBox<String> reportedByUsers;
    @FXML private TextField reporterEmail;
    @FXML private TextArea ticketComment;
    @FXML public BorderPane viewLayout;

    @FXML
    private void initialize() {
        ticketSubjects.getItems().setAll(Subject.values());
        ticketPriorities.getItems().setAll(Priority.values());
        followUpDeadline.getItems().setAll("1","2","3","4","5","6","7");

        // Lock unchangeable fields (still visible, not editable)
        ticketSubjects.setEditable(false);
        ticketSubjects.setMouseTransparent(true);
        ticketSubjects.setFocusTraversable(false);

        reportedByUsers.setEditable(false);
        reportedByUsers.setMouseTransparent(true);
        reportedByUsers.setFocusTraversable(false);

        reporterEmail.setEditable(false);
        reporterEmail.setMouseTransparent(true);
        reporterEmail.setFocusTraversable(false);

        // Removed subject listener so subject cannot be changed

        ticketPriorities.valueProperty().addListener((obs, oldV, newV) -> {
            if (currentTicket != null && newV != null) {
                currentTicket.setPriority(newV.name());
            }
        });

        ticketDescription.textProperty().addListener((obs, oldV, newV) -> {
            if (currentTicket != null) {
                currentTicket.setDescription(newV);
            }
        });

        followUpDeadline.valueProperty().addListener((obs, oldV, newV) -> {
            if (currentTicket != null) {
                currentTicket.setDeadline(deadlineFromDays(newV));
            }
        });
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.ticketService = serviceManager.getTicketService();
        this.userService = serviceManager.getUserService();
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void buildUpdateTicketForm(Ticket ticket) {
        this.currentTicket = ticket;

        // Load users for display + email lookup
        cachedUsers = userService.getAllUsers();
        reportedByUsers.getItems().setAll(
                cachedUsers.stream().map(User::getFullName).toList()
        );

        // editable fields
        ticketPriorities.getItems().stream()
                .filter(p -> p.toString().equals(ticket.getPriority()))
                .findFirst()
                .ifPresent(ticketPriorities::setValue);

        ticketDescription.setText(Optional.ofNullable(ticket.getDescription()).orElse(""));
        followUpDeadline.setValue(daysUntilDeadlineClamped(ticket.getDeadline(), 1, 7));

        // unchangeable fields
        ticketSubjects.getItems().stream()
                .filter(s -> s.toString().equals(ticket.getSubject()))
                .findFirst()
                .ifPresent(ticketSubjects::setValue);

        String reporterName = Optional.ofNullable(ticket.getReported_by_name()).orElse("");
        reportedByUsers.setValue(reporterName);

        // email from reported_by id lookup
        String email = findReporterEmail(cachedUsers, ticket).orElse("");
        reporterEmail.setText(email);
    }

    public void onUpdateTicketButtonClick(ActionEvent event) {
        if (currentTicket == null) return;

        try {
            ticketService.updateTicket(currentTicket);
            addHandledTicket(currentTicket, currentUser);
            showAlert(
                    javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Success",
                    "Ticket Updated!",
                    "Ticket successfully updated."
            );
        } catch (Exception e) {
            showAlert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Error",
                    "Could not update ticket",
                    "Something went wrong while updating the ticket.\n" + e.getMessage()
            );
        }
    }

    private void addHandledTicket(Ticket ticket, User currentUser) {
        //add service and repo logic to update handledtickets table
        HandledTicket handledTicket = new HandledTicket();
        handledTicket.setTicketId(ticket.get_id());
        handledTicket.setHandledBy(currentUser.get_id());
        handledTicket.setHandledDate(Instant.now());
        handledTicket.setComment(ticketComment.getText());

        serviceManager.getHandledTicketsService().insertHandledTicket(handledTicket);
    }

    private static String daysUntilDeadlineClamped(Instant deadline, int min, int max) {
        if (deadline == null) return null;

        long daysLeft = ChronoUnit.DAYS.between(Instant.now(), deadline);
        if (daysLeft < min) daysLeft = min;
        if (daysLeft > max) daysLeft = max;

        return String.valueOf(daysLeft);
    }

    private static Instant deadlineFromDays(String daysRaw) {
        if (daysRaw == null || daysRaw.isBlank()) return null;
        try {
            long days = Long.parseLong(daysRaw.trim());
            if (days < 0) days = 0;
            return Instant.now().plus(days, ChronoUnit.DAYS);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Optional<String> findReporterEmail(List<User> users, Ticket ticket) {
        if (ticket.getReported_by() == null) return Optional.empty();

        return users.stream()
                .filter(u -> ticket.getReported_by().equals(u.get_id()))
                .map(User::getEmail_address)
                .findFirst();
    }
    public void onCancelButtonClick(ActionEvent event) {
        mainViewController.showTickets();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
