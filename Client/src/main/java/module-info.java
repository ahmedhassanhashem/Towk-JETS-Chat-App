module gov.iti.jets {
    requires javafx.controls;
    requires javafx.fxml;
    //requires javafx.graphics;
    requires transitive javafx.graphics;
    requires java.sql;
    requires javafx.base;
    requires mysql.connector.java;
    requires java.sql.rowset;
    requires java.naming;
    requires com.zaxxer.hikari;
    requires java.rmi;
    requires java.desktop;
    opens gov.iti.jets to javafx.fxml;
    exports gov.iti.jets;
    opens gov.iti.jets.controller to javafx.fxml;
    exports gov.iti.jets.controller;
    requires jakarta.xml.bind;
    opens gov.iti.jets.config to jakarta.xml.bind;
}
