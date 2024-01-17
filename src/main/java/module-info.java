module com.example.shipsgamegui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.shipsgamegui to javafx.fxml;
    exports com.example.shipsgamegui;
}