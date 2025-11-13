package nl.inholland.student.noservicedesk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

public class NoServiceDeskApplication{
    private static AppContext context;  // only a reference before launch

    public static void launchWithContext(AppContext ctx, Stage stage) throws IOException {
        context = ctx;
        startNoDesk(stage);
    }

    public static void startNoDesk(Stage stage) throws IOException {
        // instance-level reference
        AppContext appContext = context;          // inject into instance
        //context = null;

        try {
            MongoDB db = new MongoDB(appContext);
            db.ConnectDB();
        } catch (Exception ex) {
            System.out.println("Decryption failed: wrong key: " + ex.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(NoServiceDeskApplication.class.getResource("NoServiceDeskLogin-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
