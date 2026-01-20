package nl.inholland.student.noservicedesk.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import nl.inholland.student.noservicedesk.Models.HandledTicket;
import nl.inholland.student.noservicedesk.Models.Ticket;
import nl.inholland.student.noservicedesk.Models.User;
import nl.inholland.student.noservicedesk.services.ServiceManager;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HandledTicketHistoryController {

    private ServiceManager serviceManager;
    private MainViewController mainViewController;
    private Ticket ticketHistory;

    private final ObservableList<HandledTicket> handledTickets = FXCollections.observableArrayList();

    @FXML private TableView<HandledTicket> handleTicketsHistoryTableview;
    @FXML private TableColumn<HandledTicket, String> handledByColumn;
    @FXML private TableColumn<HandledTicket, String> commentColumn;
    @FXML private Label subjectLabel;
    @FXML private Label reportedByLabel;
    @FXML private Label priorityLabel;
    @FXML private Label deadlineLabel;
    @FXML private Label descriptionLabel;

    private final Map<ObjectId, String> userNameCache = new HashMap<>();

    @FXML
    private void initialize() {
        handleTicketsHistoryTableview.setItems(handledTickets);

        handledByColumn.setCellValueFactory(cell -> {
            ObjectId id = cell.getValue().getHandledBy();
            String name = (id == null) ? "" : userNameCache.getOrDefault(id, "(unknown)");
            return new SimpleStringProperty(name);
        });

        commentColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(Optional.ofNullable(cell.getValue().getComment()).orElse(""))
        );
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void setTicket(Ticket ticketHistory) {
        this.ticketHistory = ticketHistory;

        List<HandledTicket> results = serviceManager.getHandledTicketsService().getHandledTicketHistory(ticketHistory);

        userNameCache.clear();
        for (HandledTicket ht : results) {
            ObjectId id = ht.getHandledBy();
            if (id != null && !userNameCache.containsKey(id)) {
                User u = serviceManager.getUserService().getUserById(id);
                String name = (u == null) ? "(unknown)" : u.getFullName();
                userNameCache.put(id, name);
            }
        }

        handledTickets.setAll(results);
        renderTicketInfo(ticketHistory);
    }

    public void buildHistoryTableView() {
        if (serviceManager == null || ticketHistory == null) return;
        List<HandledTicket> results =
                serviceManager.getHandledTicketsService().getHandledTicketHistory(ticketHistory);

        handledTickets.setAll(results);
        renderTicketInfo(ticketHistory);
    }

    public void onUpdateTicketButtonClick() {
        if (ticketHistory == null) return;
        mainViewController.showUpdateTicket(ticketHistory);
    }

    public void onCancelHistoryButtonClick() {
        mainViewController.showTickets();
    }

    private void renderTicketInfo(Ticket t) {
        if (t == null) return;

        String subject = Optional.ofNullable(t.getSubject()).orElse("");
        String reportedByName = Optional.ofNullable(t.getReported_by_name()).orElse("");
        String priority = Optional.ofNullable(t.getPriority()).orElse("");
        String description = Optional.ofNullable(t.getDescription()).orElse("");
        String deadline = formatInstant(t.getDeadline());

        if (subjectLabel != null) subjectLabel.setText("Subject: " + subject);
        if (reportedByLabel != null) reportedByLabel.setText("Reported By: " + reportedByName);
        if (priorityLabel != null) priorityLabel.setText("Priority: " + priority);
        if (deadlineLabel != null) deadlineLabel.setText("Deadline for Follow Up: " + deadline);
        if (descriptionLabel != null) descriptionLabel.setText("Description: " + description);
    }

    private static String formatInstant(Instant instant) {
        if (instant == null) return "";
        return instant.toString();
    }
}
