package wineshop.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Main application of the server
 * @author Mirko Piazza, Camilla Franceschini
 */
public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("WineShop Server");
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("server_icon.png"))));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}