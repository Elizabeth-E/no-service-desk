package nl.inholland.student.noservicedesk;

import nl.inholland.student.noservicedesk.config.ConfigEncryptor;
import nl.inholland.student.noservicedesk.config.ConfigLoader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

public class AppLauncher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ConfigEncryptor configEncryptor = new ConfigEncryptor();
        configEncryptor.encryptConfig();
        stage.setTitle("Enter AES Key");

        Label label = new Label("Enter AES Key (Base64):");
        PasswordField keyField = new PasswordField();
        keyField.setPrefWidth(350);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button submit = new Button("Unlock");
        submit.setDefaultButton(true);

        submit.setOnAction(e -> {
            try {
                String base64Key = keyField.getText().trim();
                if (base64Key.isEmpty()) {
                    errorLabel.setText("The key cannot be empty.");
                    return;
                }

                byte[] key = Base64.getDecoder().decode(base64Key);
                Properties props = ConfigLoader.loadEncryptedConfig("/config.enc", key);

                // Build dependency context
                AppContext context = new AppContext(props);

                // Launch the real JavaFX app with DI
                NoServiceDeskApplication.launchWithContext(context, stage);

            } catch (IllegalArgumentException ex) {
                errorLabel.setText("Invalid Base64 key.");
            } catch (Exception ex) {
                errorLabel.setText("Decryption failed: wrong key.");
            }
        });

        VBox root = new VBox(10, label, keyField, submit, errorLabel);
        root.setPadding(new Insets(12));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
