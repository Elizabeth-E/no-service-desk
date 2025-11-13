module nl.inholland.student.noservicedesk {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;
    requires javafx.graphics;
    requires bcrypt;

    opens nl.inholland.student.noservicedesk to javafx.fxml;
    exports nl.inholland.student.noservicedesk;
    exports nl.inholland.student.noservicedesk.Controllers;
    opens nl.inholland.student.noservicedesk.Controllers to javafx.fxml;
    exports nl.inholland.student.noservicedesk.config;
    opens nl.inholland.student.noservicedesk.config to javafx.fxml;
    exports nl.inholland.student.noservicedesk.database;
    opens nl.inholland.student.noservicedesk.database to javafx.fxml;
}