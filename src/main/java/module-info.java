module nl.inholland.student.noservicedesk {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens nl.inholland.student.noservicedesk to javafx.fxml;
    exports nl.inholland.student.noservicedesk;
}