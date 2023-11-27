module com.example.shipsgamegui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.shipsgamegui to javafx.fxml;
    exports com.example.shipsgamegui;
}