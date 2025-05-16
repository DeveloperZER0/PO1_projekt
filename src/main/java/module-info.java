module com.healthtracker.healthtracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens com.healthtracker to javafx.fxml;
    exports com.healthtracker;
}