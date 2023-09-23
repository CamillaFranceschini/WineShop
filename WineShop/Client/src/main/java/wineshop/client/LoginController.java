package wineshop.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import wineshop.model.Customer;
import wineshop.model.Employee;
import wineshop.model.GlobalVarAndUtilities;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Controller Class for managing the Login and SignUp
 * @author Mirko Piazza, Camilla Franceschini
 */
public class LoginController {
    /**
     * Client for server connection
     */
    private Client client;

    /**
     * Zone to declare all javafx objects
     */
    @FXML
    private Label loginResultLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox flagEmployee;
    @FXML
    private TextField nameSignUp;
    @FXML
    private TextField surnameSignUp;
    @FXML
    private TextField NINSignUp;
    @FXML
    private TextField emailSignUp;
    @FXML
    private TextField phoneSignUp;
    @FXML
    private TextField addressSignUp;
    @FXML
    private TextField usernameSignUp;
    @FXML
    private TextField passwordSignUp;
    @FXML
    private TextField confirmPasswordSignUp;
    @FXML
    private Button signUpButton;
    @FXML
    private Label signUpResultLabel;

    /**
     * Try login by a User based on the values of the input fields
     * @param ae The action event to permit the ViewHandler Class to close this stage
     */
    @FXML
    protected void onLoginButtonClick(final ActionEvent ae) {
        client = new Client();
        //ViewHandler.openApplication(ae, new Customer(1, "mpiazza"), false); //customer
        //ViewHandler.openApplication(ae, new Employee(2, "ccamillini"), true); //employee
        //ViewHandler.openApplication(ae, new Employee(1, "mcostanzo"), true); //admin
        String user = usernameField.getText();
        String pwd = passwordField.getText();
        Boolean employee = flagEmployee.isSelected();
        int id = client.login(user, pwd, employee);

        if(id != -1) {
            if(employee) {
                ViewHandler.openApplication(ae, new Employee(id, user), true);
            } else {
                ViewHandler.openApplication(ae, new Customer(id, user), false);
            }
        }
        else {
            loginResultLabel.setText("Incorrect username or password");
        }
        client.closeConnection();
    }

    /**
     * Register a new Customer based on the values of the input fields
     */
    @FXML
    protected void onSignUpButtonClick() {
        client = new Client();
        if (nameSignUp.getText().length() == 0 || surnameSignUp.getText().length() == 0 || NINSignUp.getText().length() == 0 || emailSignUp.getText().length() == 0 || phoneSignUp.getText().length() == 0 || addressSignUp.getText().length() == 0 || usernameSignUp.getText().length() == 0 || passwordSignUp.getText().length() == 0) {
            client.clientPopup("Enter a value in each field", true);
        } else if(NINSignUp.getText().length() != 16) {
            client.clientPopup("Enter a valid national insurance number", true);
        } else if (!Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$").matcher(emailSignUp.getText()).matches()) {
            client.clientPopup("Enter a valid email", true);
        } else if (phoneSignUp.getText().length() != 10 || !GlobalVarAndUtilities.isNumeric(phoneSignUp.getText())) {
            client.clientPopup("Enter a valid phone number", true);
        } else if (!Objects.equals(passwordSignUp.getText(),confirmPasswordSignUp.getText())) {
            client.clientPopup("Passwords don't match", true);
        } else {
            Customer c = new Customer(nameSignUp.getText(), surnameSignUp.getText(), NINSignUp.getText().toUpperCase(), emailSignUp.getText(), phoneSignUp.getText(), addressSignUp.getText(), usernameSignUp.getText(), passwordSignUp.getText());
            if(client.signUp(c)) {
                signUpResultLabel.setText("Registration complete... You can now login");
                signUpButton.setDisable(true);
            }
            else {
                signUpResultLabel.setText("Request failed, please try again later");
            }
        }
        client.closeConnection();
    }
}