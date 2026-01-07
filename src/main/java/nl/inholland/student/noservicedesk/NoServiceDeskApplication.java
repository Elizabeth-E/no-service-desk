package nl.inholland.student.noservicedesk;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.inholland.student.noservicedesk.Controllers.LoginController;
import nl.inholland.student.noservicedesk.database.MongoConn;
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
        MongoConn db = new MongoConn(appContext);
        db.connectDB();
        ServiceManager serviceManager = new ServiceManager(db);

        FXMLLoader fxmlLoader = new FXMLLoader(NoServiceDeskApplication.class.getResource("NoServiceDeskLogin-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 800, 800);

        // ✅ controller should match the login FXML fx:controller
        LoginController controller = fxmlLoader.getController();

        controller.setServiceManager(serviceManager);

        stage.setTitle("Welcome to NoServiceDesk");
        stage.setScene(scene);
        stage.show();
    }

}
