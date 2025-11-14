package nl.inholland.student.noservicedesk;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
       //this context is for the decrypted config file
        AppContext appContext = context;
        MongoDB db = new MongoDB(appContext);
        ServiceManager serviceManager = new ServiceManager(db);

        try {
            db.connectDB();
        } catch (Exception ex) {
            System.out.println("Database connection failed: " + ex.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(NoServiceDeskApplication.class.getResource("/nl/inholland/student/noservicedesk/NoServiceDeskLogin-view.fxml"));
        MainViewController mainViewController = new MainViewController(stage, serviceManager);
        fxmlLoader.setController(mainViewController);
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
