package nl.inholland.student.noservicedesk.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.NoServiceDeskApplication;
import nl.inholland.student.noservicedesk.services.ServiceManager;

public class MainViewController {

    @FXML private BorderPane root;

    private Stage stage;
    private ServiceManager serviceManager;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @FXML
    public void showDashboard() {
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
                tc.fillTicketsTable();
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

    private void setCenter(String fxml, java.util.function.Consumer<Object> afterLoad) {
        try {
            FXMLLoader loader = new FXMLLoader(NoServiceDeskApplication.class.getResource(fxml));
            Parent view = loader.load();
            Object controller = loader.getController();

            afterLoad.accept(controller);
            root.setCenter(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
