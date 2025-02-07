module gov.iti.jets {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires javafx.base;
    requires mysql.connector.java;
    requires java.sql.rowset;
    requires com.zaxxer.hikari;
    requires java.rmi;
    requires java.naming;
    opens gov.iti.jets to javafx.fxml;
    exports gov.iti.jets;
    opens gov.iti.jets.controller to javafx.fxml;
    exports gov.iti.jets.controller;
    opens gov.iti.jets.dao to java.rmi;
    exports gov.iti.jets.dao;
    requires jakarta.xml.bind;
    requires org.json;
	requires okhttp3;
    exports gov.iti.jets.chatbot;
    opens gov.iti.jets.config to jakarta.xml.bind;
}