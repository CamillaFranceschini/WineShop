package wineshop.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import wineshop.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Controller Class for managing the Employee view
 * @author Mirko Piazza, Camilla Franceschini
 */
public class EmployeeController {
    /**
     * Client for server connection
     */
    private Client client;
    /**
     * Employee logged
     */
    private Employee e;
    /**
     * Order to manage
     */
    private Order o;
    /**
     * Customer Order's states visible from an Employee
     */
    private String[] choiceOrderCustomer = {GlobalVarAndUtilities.orderStates.Completato.toString(), GlobalVarAndUtilities.orderStates.Richiesto.toString(), GlobalVarAndUtilities.orderStates.ConfermaUtente.toString(), GlobalVarAndUtilities.orderStates.Annullato.toString(), GlobalVarAndUtilities.orderStates.Scaduto.toString()};
    /**
     * Supplier Order's states visible from an Employee
     */
    private String[] choiceOrderSupplier = {GlobalVarAndUtilities.orderStates.Completato.toString()};
    /**
     * Token to handle the admin privileges of an Employee
     */
    private boolean tokenAdmin = false;
    /**
     * Token for avoid to manage orders multiple times
     */
    private boolean tokenOrder = false;
    /**
     * Wine bottles in stock of highlighted Wine
     */
    private int stock;
    /**
     * Array to handle the views switch by AnchorPane's list
     */
    private ArrayList<AnchorPane> anchorPanes;

    /**
     * Zone to declare all javafx objects
     */
    @FXML
    private AnchorPane apHome;
    @FXML
    private AnchorPane apWine;
    @FXML
    private AnchorPane apCustomer;
    @FXML
    private AnchorPane apOrderCustomer;
    @FXML
    private AnchorPane apOrderCustomerDetails;
    @FXML
    private AnchorPane apOrderSupplier;
    @FXML
    private AnchorPane apPassword;
    @FXML
    private AnchorPane apAddEmployee;
    @FXML
    private AnchorPane apDeleteEmployee;
    @FXML
    private AnchorPane apReport;
    @FXML
    private TableView wineTV;
    @FXML
    private TextField nameField;
    @FXML
    private TextField yearField;
    @FXML
    private TableView customerTV;
    @FXML
    private TextField surnameField;
    @FXML
    private TableView orderCustomerTV;
    @FXML
    private TableView orderSupplierTV;
    @FXML
    private ComboBox stateCustomerCombo;
    @FXML
    private ComboBox stateSupplierCombo;
    @FXML
    private DatePicker startDateCustomerField;
    @FXML
    private DatePicker endDateCustomerField;
    @FXML
    private DatePicker startDateSupplierField;
    @FXML
    private DatePicker endDateSupplierField;
    @FXML
    private TextField orderId;
    @FXML
    private TextField orderWine;
    @FXML
    private TextField orderQuantity;
    @FXML
    private TextField orderPrice;
    @FXML
    private TextField orderDate;
    @FXML
    private TextField orderCustomer;
    @FXML
    private TextField orderAssegnation;
    @FXML
    private Button checkAvailabilityWineButton;
    @FXML
    private AnchorPane apRefill;
    @FXML
    private TextField refillValue;
    @FXML
    private AnchorPane apDelivery;
    @FXML
    private TextField deliveryValue;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField nameAddEmployee;
    @FXML
    private TextField surnameAddEmployee;
    @FXML
    private TextField NINAddEmployee;
    @FXML
    private TextField emailAddEmployee;
    @FXML
    private TextField phoneAddEmployee;
    @FXML
    private TextField addressAddEmployee;
    @FXML
    private TextField usernameAddEmployee;
    @FXML
    private TextField passwordAddEmployee;
    @FXML
    private TextField confirmPasswordAddEmployee;
    @FXML
    private CheckBox isAdminAddEmployee;
    @FXML
    private Button addEmployeeButton;
    @FXML
    private TableView employeeTV;
    @FXML
    private TextField usernameField;
    @FXML
    private DatePicker startDateReportField;
    @FXML
    private DatePicker endDateReportField;
    @FXML
    private TextField incomeField;
    @FXML
    private TextField costField;
    @FXML
    private TextField bottlesSoldField;
    @FXML
    private TextField bottlesLeftField;
    @FXML
    private TableView bottlesSoldTV;
    @FXML
    private TableView bottlesLeftTV;
    @FXML
    private TableView avgReviewTV;
    @FXML
    private TableView orderTypesTV;

    /**
     * Check if the Employee is an admin
     * @return True if the Employee is an admin, false otherwise
     */
    private boolean checkIfAdmin()
    {
        if(tokenAdmin) { return true; }

        e = (Employee) ViewHandler.u;
        if(client.checkIfAdmin(e)) {
            tokenAdmin = true;
            return true;
        } else {
            tokenAdmin = false;
            return false;
        }
    }

    /**
     * Set the view to show
     * @param ap The AnchorPane to show
     */
    private void setVisibility(AnchorPane ap)
    {
        anchorPanes = new ArrayList<AnchorPane>(Arrays.asList(apHome, apCustomer, apWine, apOrderCustomer, apOrderSupplier, apPassword, apAddEmployee, apDeleteEmployee, apReport, apOrderCustomerDetails));
        ap.setVisible(true);
        for (int i = 0; i < anchorPanes.size(); i++){
            if(ap.getId() != anchorPanes.get(i).getId()) {
                anchorPanes.get(i).setVisible(false);
            }
        }
    }

    /**
     * Set the TableViews
     * @param view The view for manage which fields to show in the TableView
     * @param tv The TableView to populate
     * @param list The list of objects for populate the TableView
     */
    private void setTableView(String view, TableView tv, ArrayList<?> list)
    {
        ArrayList<String> values = new ArrayList<>();
        tv.getColumns().clear();
        tv.getItems().clear();
        TableColumn<Wine, String> tc;
        if(view == "EmployeeWineSearch") {
            values.add(0, "name");
            values.add(1, "producer");
            values.add(2, "year");
            values.add(3, "purchasePrice");
            values.add(4, "salePrice");
            values.add(5, "courierName");
            values.add(6, "supplierName");
        } else if (view == "EmployeeCustomerSearch") {
            values.add(0, "username");
            values.add(1, "name");
            values.add(2, "surname");
            values.add(3, "email");
            values.add(4, "phone");
            values.add(5, "address");
        } else if (view == "EmployeeOrderCustomerSearch") {
            if (Objects.equals(stateCustomerCombo.getSelectionModel().getSelectedItem().toString(), GlobalVarAndUtilities.orderStates.Completato.toString())) {
                values.add(0, "idOrder");
                values.add(1, "wineName");
                values.add(2, "quantity");
                values.add(3, "price");
                values.add(4, "customerUsername");
                values.add(5, "orderDate");
                values.add(6, "employeeUsername");
                values.add(7, "deliveryDate");
                values.add(8, "reviewEmployee");
            } else if (Objects.equals(stateCustomerCombo.getSelectionModel().getSelectedItem().toString(), GlobalVarAndUtilities.orderStates.ConfermaUtente.toString()) || Objects.equals(stateCustomerCombo.getSelectionModel().getSelectedItem().toString(), GlobalVarAndUtilities.orderStates.Annullato.toString())) {
                values.add(0, "idOrder");
                values.add(1, "wineName");
                values.add(2, "quantity");
                values.add(3, "price");
                values.add(4, "customerUsername");
                values.add(5, "orderDate");
                values.add(6, "employeeUsername");
                values.add(7, "proposalDelivery");
            } else {
                values.add(0, "idOrder");
                values.add(1, "wineName");
                values.add(2, "quantity");
                values.add(3, "price");
                values.add(4, "customerUsername");
                values.add(5, "orderDate");
                values.add(6, "assignationDate");
                values.add(7, "employeeUsername");
            }
        } else if (view == "EmployeeOrderSupplierSearch") {
            values.add(0, "idOrder");
            values.add(1, "wineName");
            values.add(2, "quantity");
            values.add(3, "price");
            values.add(4, "orderDate");
            values.add(5, "deliveryDate");
        } else if (view == "EmployeeEmployeeSearch") {
            values.add(0, "username");
            values.add(1, "name");
            values.add(2, "surname");
            values.add(3, "email");
            values.add(4, "phone");
            values.add(5, "address");
            values.add(6, "admin");
        } else if (view == "EmployeeGenerateReportClick_wineBottles") {
            values.add(0, "name");
            values.add(1, "quantity");
        } else if (view == "EmployeeGenerateReportClick_avgReviewEmployees") {
            values.add(0, "username");
            values.add(1, "avgReview");
        } else if (view == "EmployeeGenerateReportClick_countOrdersType") {
            values.add(0, "type");
            values.add(1, "state");
            values.add(2, "quantity");
        }
        for (int i = 0 ; i < values.size() ; i++) {
            tc = new TableColumn<>(values.get(i));
            tc.setCellValueFactory(new PropertyValueFactory<>(values.get(i)));
            double width = (int) (tv.getWidth() / values.size());
            tc.setPrefWidth(width);
            tv.getColumns().add(i,tc);
        }
        if(list != null) {
            for (Object o : list) {
                tv.getItems().add(o);
            }
        }
    }

    /**
     * Lock the calendars for avoid to insert text manually
     */
    @FXML
    protected void LockCalendar()
    {
        startDateCustomerField.getEditor().setEditable(false);
        endDateCustomerField.getEditor().setEditable(false);
        startDateSupplierField.getEditor().setEditable(false);
        endDateSupplierField.getEditor().setEditable(false);
        startDateReportField.getEditor().setEditable(false);
        endDateReportField.getEditor().setEditable(false);
    }

    /**
     * Show Home's view
     */
    @FXML
    protected void onHomeClick() {
        setVisibility(apHome);
    }

    /**
     * Show Wines's view and wines list (eventually filtered)
     */
    @FXML
    protected void onWineSearchClick() {
        setVisibility(apWine);
        client = new Client();
        ArrayList<Wine> winelist;
        String name = nameField.getText();
        String year = yearField.getText();
        if(GlobalVarAndUtilities.isNumeric(year) || year.isEmpty()) {
            winelist = client.listWines(name, year);
        } else {
            client.clientPopup("Enter a year", true);
            winelist = client.listWines(name, "");
        }
        setTableView("EmployeeWineSearch", wineTV, winelist);
        client.closeConnection();
    }

    /**
     * Show Customers's view and customers list (eventually filtered)
     */
    @FXML
    protected void onCustomerSearchClick() {
        setVisibility(apCustomer);
        client = new Client();
        ArrayList<Customer> customerlist;
        String surname = surnameField.getText();
        customerlist = client.listCustomers(surname);
        setTableView("EmployeeCustomerSearch", customerTV, customerlist);
        client.closeConnection();
    }

    /**
     * Show Orders Customers's view and orders list (eventually filtered)
     */
    @FXML
    protected void onOrderCustomerSearchClick() {
        setVisibility(apOrderCustomer);
        LockCalendar();
        client = new Client();
        ArrayList<Order> orderlist;
        if(stateCustomerCombo.getItems().size() == 0) {
            stateCustomerCombo.getItems().addAll(choiceOrderCustomer);
            stateCustomerCombo.setValue(choiceOrderCustomer[0]);
        }
        orderlist = client.listOrdersEmployee(stateCustomerCombo.getSelectionModel().getSelectedItem().toString(), startDateCustomerField.getEditor().getText(), endDateCustomerField.getEditor().getText(), true);
        setTableView("EmployeeOrderCustomerSearch", orderCustomerTV, orderlist);
        client.closeConnection();
    }

    /**
     * Show Orders Suppliers's view and orders list (eventually filtered)
     */
    @FXML
    protected void onOrderSupplierSearchClick() {
        setVisibility(apOrderSupplier);
        LockCalendar();
        client = new Client();
        ArrayList<Order> orderlist;
        if(stateSupplierCombo.getItems().size() == 0) {
            stateSupplierCombo.getItems().addAll(choiceOrderSupplier);
            stateSupplierCombo.setValue(choiceOrderSupplier[0]);
        }
        orderlist = client.listOrdersEmployee(stateSupplierCombo.getSelectionModel().getSelectedItem().toString(), startDateSupplierField.getEditor().getText(), endDateSupplierField.getEditor().getText(), false);
        setTableView("EmployeeOrderSupplierSearch", orderSupplierTV, orderlist);
        client.closeConnection();
    }

    /**
     * Highlight an Order to give the possibility to handle it
     */
    @FXML
    protected void onOrderTVClick() {
        if(tokenOrder) {
            onOrderCustomerSearchClick();
            tokenOrder = false;
            return;
        }
        o = (Order) orderCustomerTV.getSelectionModel().getSelectedItem();
        e = (Employee) ViewHandler.u;
        //An Employee can manage only a requested Order
        if(o != null && Objects.equals(o.getState(), GlobalVarAndUtilities.orderStates.Richiesto.toString()) && Objects.equals(o.getEmployeeUsername(), e.getUsername())) {
            setVisibility(apOrderCustomerDetails);
            checkAvailabilityWineButton.setVisible(true);
            apRefill.setVisible(false);
            apDelivery.setVisible(false);
            deliveryValue.setText(String.valueOf(3));
            orderId.setText(String.valueOf(o.getIdOrder()));
            orderWine.setText(o.getWineName());
            orderQuantity.setText(String.valueOf(o.getQuantity()));
            orderPrice.setText(String.valueOf(o.getPrice()));
            orderDate.setText(String.valueOf(o.getOrderDate()));
            orderCustomer.setText(o.getCustomerUsername());
            orderAssegnation.setText(String.valueOf(o.getAssignationDate()));
            tokenOrder = true;
        }
    }

    /**
     * Check if the Wine of an Order is available
     */
    @FXML
    protected void onCheckAvailabilityWineClick() {
        client = new Client();
        stock = client.checkAvailabilityWine(o);
        if (stock == -999) {
            client.clientPopup("Request failed, please try again later", true);
        } else if(stock < 0) {
            client.clientPopup("It is necessary to carry out a refill before continuing with the order", true);
            checkAvailabilityWineButton.setVisible(false);
            apRefill.setVisible(true);
            refillValue.setText(String.valueOf(-stock));
        } else {
            client.clientPopup("Wine availability ok, it is possible proceed with the order", false);
            checkAvailabilityWineButton.setVisible(false);
            apDelivery.setVisible(true);
        }
        client.closeConnection();
    }

    /**
     * Send an Order to the Customer confirmation based on the values of the input fields
     */
    @FXML
    protected void onSendOrderButtonClick() {
        client = new Client();
        if(client.sendOrder(o, Integer.valueOf(deliveryValue.getText()))) {
            client.clientPopup("Order sent to the customer", false);
            client.closeConnection();
            setVisibility(apOrderCustomer);
        } else {
            client.clientPopup("Request failed, please try again later", true);
        }
    }

    /**
     * Decreases the days of delivery
     */
    @FXML
    protected void onDecreaseDeliveryButtonClick() {
        if (Integer.valueOf(deliveryValue.getText()) > 3) {
            deliveryValue.setText(String.valueOf(Integer.valueOf(deliveryValue.getText()) - 1));
        } else {
            client.clientPopup("Unable to decrease the delivery proposal, enter at least 3 days to allow the shipment to be carried out", true);
        }
    }

    /**
     * Increases the days of delivery
     */
    @FXML
    protected void onIncreaseDeliveryButtonClick() {
        deliveryValue.setText(String.valueOf(Integer.valueOf(deliveryValue.getText()) + 1));
    }

    /**
     * Refill the Wine in the stock based on the values of the input fields
     */
    @FXML
    protected void onRefillButtonClick() {
        client = new Client();
        if(client.refillWine(o, -stock)) {
            client.clientPopup("Wine acquired in stock, it is now possible to proceed with the management of the order", false);
            apRefill.setVisible(false);
            apDelivery.setVisible(true);
        } else {
            client.clientPopup("Request failed, please try again later", true);
        }
        client.closeConnection();
    }

    /**
     * Decreases the quantity of Wine to buy for refill stock
     */
    @FXML
    protected void onDecreaseRefillButtonClick() {
        if (Integer.valueOf(refillValue.getText()) > -stock) {
            refillValue.setText(String.valueOf(Integer.valueOf(refillValue.getText()) - 1));
        } else {
            client.clientPopup("Unable to decrease the quantity of refill due to the minimum wine threshold to have in stock", true);
        }
    }

    /**
     * Increases the quantity of Wine to buy for refill stock
     */
    @FXML
    protected void onIncreaseRefillButtonClick() {
        refillValue.setText(String.valueOf(Integer.valueOf(refillValue.getText()) + 1));
    }

    /**
     * Show Profile Change Password's view
     */
    @FXML
    protected void onPasswordChangeClick() {
        setVisibility(apPassword);
        passwordField.setText("");
    }

    /**
     * Change password based on the values of the input fields
     */
    @FXML
    protected void onPasswordApplyClick() {
        client = new Client();
        e = (Employee) ViewHandler.u;
        String password = passwordField.getText();
        if(password.isEmpty()) {
            client.clientPopup("Enter at least one character", true);
        } else {
            if (client.changePassword(e, password)) {
                client.clientPopup("Password has been changed", false);
                passwordField.setText("");
            } else {
                client.clientPopup("Request failed, please try again later", true);
            }
        }
        client.closeConnection();
    }

    /**
     * Show Admin Add Employee's view
     */
    @FXML
    protected void onAddEmployeeClick() {
        client = new Client();
        if(checkIfAdmin()) {
            setVisibility(apAddEmployee);
            addEmployeeButton.setDisable(false);
            nameAddEmployee.setText("");
            surnameAddEmployee.setText("");
            NINAddEmployee.setText("");
            emailAddEmployee.setText("");
            phoneAddEmployee.setText("");
            addressAddEmployee.setText("");
            usernameAddEmployee.setText("");
            passwordAddEmployee.setText("");
            confirmPasswordAddEmployee.setText("");
            isAdminAddEmployee.setSelected(false);
        } else {
            client.clientPopup("You do not have administrator privileges, return to the home", true);
            onHomeClick();
        }
        client.closeConnection();
    }

    /**
     * Add a new Employee based on the values of the input fields
     */
    @FXML
    protected void onConfirmAddEmployeeClick() {
        client = new Client();
        if (nameAddEmployee.getText().length() == 0 || surnameAddEmployee.getText().length() == 0 || NINAddEmployee.getText().length() == 0 || emailAddEmployee.getText().length() == 0 || phoneAddEmployee.getText().length() == 0 || addressAddEmployee.getText().length() == 0 || usernameAddEmployee.getText().length() == 0 || passwordAddEmployee.getText().length() == 0) {
            client.clientPopup("Enter a value in each field", true);
        } else if(NINAddEmployee.getText().length() != 16) {
            client.clientPopup("Enter a valid national insurance number", true);
        } else if (!Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$").matcher(emailAddEmployee.getText()).matches()) {
            client.clientPopup("Enter a valid email", true);
        } else if (phoneAddEmployee.getText().length() != 10 || !GlobalVarAndUtilities.isNumeric(phoneAddEmployee.getText())) {
            client.clientPopup("Enter a valid phone number", true);
        } else if (!Objects.equals(passwordAddEmployee.getText(),confirmPasswordAddEmployee.getText())) {
            client.clientPopup("Passwords don't match", true);
        } else {
            Employee newEmployee = new Employee(nameAddEmployee.getText(), surnameAddEmployee.getText(), NINAddEmployee.getText().toUpperCase(), emailAddEmployee.getText(), phoneAddEmployee.getText(), addressAddEmployee.getText(), usernameAddEmployee.getText(), passwordAddEmployee.getText(), isAdminAddEmployee.isSelected());
            if (client.addEmployee(newEmployee)) {
                client.clientPopup("Employee has been added", false);
                addEmployeeButton.setDisable(true);
            } else {
                client.clientPopup("Request failed, please try again later", true);
            }
        }
        client.closeConnection();
    }

    /**
     * Show Admin Delete Employee's view and employees list (eventually filtered)
     */
    @FXML
    protected void onDeleteEmployeeClick() {
        client = new Client();
        if(checkIfAdmin()) {
            setVisibility(apDeleteEmployee);
            client = new Client();
            ArrayList<Employee> employeelist;
            String username = usernameField.getText();
            employeelist = client.listEmployees(username);
            setTableView("EmployeeEmployeeSearch", employeeTV, employeelist);
            client.closeConnection();
        } else {
            client.clientPopup("You do not have administrator privileges, return to the home", true);
            onHomeClick();
        }
        client.closeConnection();
    }

    /**
     * Delete an Employee selected
     */
    @FXML
    protected void onConfirmDeleteEmployeeClick() {
        client = new Client();
        e = (Employee) ViewHandler.u;
        Employee employeeToDelete = (Employee) employeeTV.getSelectionModel().getSelectedItem();
        if (employeeToDelete != null && Objects.equals(employeeToDelete.getUsername(), e.getUsername())) {
            client.clientPopup("Unable to delete own user", true);
        } else if(employeeToDelete != null) {
            if(client.deleteEmployee(employeeToDelete)) {
                onDeleteEmployeeClick();
                client.clientPopup("Employee has been deleted", false);
            } else {
                client.clientPopup("Request failed, please try again later", true);
            }
        } else {
            client.clientPopup("Select a employee to delete", true);
        }
        client.closeConnection();
    }

    /**
     * Show Admin Report's view
     */
    @FXML
    protected void onReportClick() {
        client = new Client();
        if(checkIfAdmin()) {
            setVisibility(apReport);
            LockCalendar();
        } else {
            client.clientPopup("You do not have administrator privileges, return to the home", true);
            onHomeClick();
        }
        client.closeConnection();
    }

    /**
     * Generate and show all info of the report
     */
    @FXML
    protected void onGenerateReportClick() {
        client = new Client();
        Double income = client.generateIncomeCount(startDateReportField.getEditor().getText(), endDateReportField.getEditor().getText());
        incomeField.setText(income + " €");
        client.closeConnection();
        client = new Client();
        Double cost = client.generateCostCount(startDateReportField.getEditor().getText(), endDateReportField.getEditor().getText());;
        costField.setText(cost + " €");
        client.closeConnection();
        client = new Client();
        int bottlesSold = client.generateAllBottlesSold(startDateReportField.getEditor().getText(), endDateReportField.getEditor().getText());;
        bottlesSoldField.setText(String.valueOf(bottlesSold));
        client.closeConnection();
        client = new Client();
        int bottlesLeft = client.generateAllBottlesLeft(endDateReportField.getEditor().getText());;
        bottlesLeftField.setText(String.valueOf(bottlesLeft));
        client.closeConnection();
        client = new Client();
        ArrayList<Wine> wineBottlesSold = client.wineBottlesSold(startDateReportField.getEditor().getText(), endDateReportField.getEditor().getText());
        setTableView("EmployeeGenerateReportClick_wineBottles", bottlesSoldTV, wineBottlesSold);
        client.closeConnection();
        client = new Client();
        ArrayList<Wine> wineBottlesLeft = client.wineBottlesLeft(endDateReportField.getEditor().getText());
        setTableView("EmployeeGenerateReportClick_wineBottles", bottlesLeftTV, wineBottlesLeft);
        client.closeConnection();
        client = new Client();
        ArrayList<Employee> avgReviewEmployees = client.avgReviewEmployees(startDateReportField.getEditor().getText(), endDateReportField.getEditor().getText());
        setTableView("EmployeeGenerateReportClick_avgReviewEmployees", avgReviewTV, avgReviewEmployees);
        client.closeConnection();
        client = new Client();
        ArrayList<Order> countOrdersType = client.countOrderTypes(startDateReportField.getEditor().getText(), endDateReportField.getEditor().getText());;
        setTableView("EmployeeGenerateReportClick_countOrdersType", orderTypesTV, countOrdersType);
        client.closeConnection();
    }
}