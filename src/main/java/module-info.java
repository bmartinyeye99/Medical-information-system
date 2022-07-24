module com.example.medis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires com.fasterxml.jackson.dataformat.xml;
    requires org.jsoup;

    exports com.medis;
    opens com.medis to javafx.fxml;
    exports com.medis.controllers;
    opens com.medis.controllers to javafx.fxml;
    exports com.medis.models;
    opens com.medis.models to javafx.fxml;
}