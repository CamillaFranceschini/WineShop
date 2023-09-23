package wineshop.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import wineshop.model.User;

import java.io.IOException;
import java.util.Objects;

/**
 * Class for manage the switch from login stage to application stage
 * @author Mirko Piazza, Camilla Franceschini
 */
public class ViewHandler {
    /**
     * User logged
     */
    public static User u;

    /**
     * Close the old stage and open the new one
     * @param ae The action event to close old stage
     * @param user The user logged
     * @param employee True if the User is an Employee, false if the User is a Customer
     */
    public static void openApplication(final ActionEvent ae, User user, Boolean employee) {
        try {
            FXMLLoader fxmlLoader;
            Scene scene;
            Stage stage = new Stage();
            Node source = (Node) ae.getSource();
            Stage oldStage = (Stage) source.getScene().getWindow();
            oldStage.close();
            if(employee) {
                fxmlLoader = new FXMLLoader(ViewHandler.class.getResource("employee.fxml"));
                stage.setTitle("WineShop for employees");
            } else {
                fxmlLoader = new FXMLLoader(ViewHandler.class.getResource("customer.fxml"));
                stage.setTitle("WineShop");
            }
            scene = new Scene(fxmlLoader.load(), 700, 500);
            u = user;
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(ViewHandler.class.getResourceAsStream("client_icon.png"))));
            stage.setResizable(false);
            stage.show();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
