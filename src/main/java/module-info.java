module com.healthtracker.healthtracker {
    requires java.naming;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires eu.hansolo.tilesfx;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires net.bytebuddy;
    requires jbcrypt;

    opens com.healthtracker to javafx.fxml;
    opens com.healthtracker.ui to javafx.fxml;
    opens com.healthtracker.ui.components to javafx.fxml;
    opens com.healthtracker.model to org.hibernate.orm.core, net.bytebuddy;
    opens com.healthtracker.util to org.hibernate.orm.core;
    exports com.healthtracker;
    exports com.healthtracker.model;
    exports com.healthtracker.ui;
    exports com.healthtracker.ui.components;
}