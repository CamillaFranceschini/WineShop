module wineshop.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires Model;


    opens wineshop.client to javafx.fxml;
    exports wineshop.client;
}