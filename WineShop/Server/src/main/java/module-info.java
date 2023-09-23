module wineshop.server {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires Model;


    opens wineshop.server to javafx.fxml;
    exports wineshop.server;
}