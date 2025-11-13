package nl.inholland.student.noservicedesk;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Controllers.MainViewController;
import nl.inholland.student.noservicedesk.database.MongoDB;
import nl.inholland.student.noservicedesk.services.ServiceManager;

import java.io.IOException;

public class NoServiceDeskApplication{
    private static AppContext context;

    public static void launchWithContext(AppContext ctx, Stage stage) throws IOException {
        context = ctx;
        startNoDesk(stage);
    }

    public static void startNoDesk(Stage stage) throws IOException {
        AppContext appContext = context;
        MongoDB db = new MongoDB(appContext);
        ServiceManager serviceManager = new ServiceManager(db);
        MainViewController controller;

        FXMLLoader fxmlLoader = new FXMLLoader(NoServiceDeskApplication.class.getResource("/nl/inholland/student/noservicedesk/NoServiceDeskLogin-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        controller = fxmlLoader.getController();
        controller.setStage(stage);
        controller.setServiceManager(serviceManager);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
