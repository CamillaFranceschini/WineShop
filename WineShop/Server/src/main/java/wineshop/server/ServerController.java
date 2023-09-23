package wineshop.server;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Controller Class for managing server
 * @author Mirko Piazza, Camilla Franceschini
 */
public class ServerController {
    /**
     * Server for client connection
     */
    private Server server = null;

    /**
     * Zone to declare all javafx objects
     */
    @FXML
    private Label state_server;

    /**
     * Turn on the main server
     * @throws IOException
     */
    @FXML
    protected void onStartButtonClick() throws IOException {
        if(server == null) {
            server = new Server();
            new Thread(server).start();
            state_server.setText("Server started");
        } else {
            state_server.setText("Server already started");
        }
    }

    /**
     * Turn off the main server
     */
    @FXML
    protected void onStopButtonClick() {
        if(server != null) {
            server.stop();
            server = null;
            state_server.setText("Server stopped");
        } else {
            state_server.setText("Server already closed");
        }
    }
}