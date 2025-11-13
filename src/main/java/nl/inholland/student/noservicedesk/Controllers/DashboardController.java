package nl.inholland.student.noservicedesk.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.services.ServiceManager;

public class DashboardController {

    @FXML
    private Label unresolvedIncidentsLabel;
    private ServiceManager serviceManager;
    private Stage stage;


    public DashboardController(Stage stage, ServiceManager services) {
        this.stage = stage;
        this.serviceManager = services;;
    }

    public void setTestLabel(){
        unresolvedIncidentsLabel.setText("0/100");
    }

}
