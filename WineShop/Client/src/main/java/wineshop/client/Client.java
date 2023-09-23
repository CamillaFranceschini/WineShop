package wineshop.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import wineshop.model.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class for connection and communication with the server sockets
 * @author Mirko Piazza, Camilla Franceschini
 */
public class Client {
    /**
     * Server's hostname
     */
    private static final String SHOST = "localhost";
    /**
     * Socket for client connection
     */
    private Socket client;
    /**
     * Output stream for client connection
     */
    private ObjectOutputStream os;
    /**
     * Input stream for client connection
     */
    private ObjectInputStream is;

    /**
     * Constructor to instantiate a new client
     */
    public Client()
    {
        try
        {
            client = new Socket(SHOST, GlobalVarAndUtilities.SPORT);
            os = new ObjectOutputStream(client.getOutputStream());
            os.flush();
            is = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Close client connection
     */
    public void closeConnection()
    {
        try
        {
            os.close();
            is.close();
            client.close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Send an object to server
     * @param oSend The object to send
     */
    public boolean sendObject(Object oSend)
    {
        try
        {
            os.writeObject(oSend);
            os.flush();
            os.reset();
        }
        catch (IOException ex)
        {
            return false;
        }
        return true;
    }

    /**
     * Awaits object arrival
     * @return The received object
     */
    public Object receiveObject()
    {
        Object oReceive;
        try
        {
            oReceive = is.readObject();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            oReceive = null;
        }
        return oReceive;
    }

    /**
     * Register a new Customer
     * @param c The new Customer
     * @return True if the operation was successful, false otherwise
     */
    public boolean signUp(Customer c)
    {
        sendObject("signUp");

        sendObject(c);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Try login by a User
     * @param user The username of the User
     * @param pwd The password of the User
     * @param employee True if the User is an Employee, false if the User is a Customer
     * @return The unique ID of the user. -1 if the login failed
     */
    public int login(String user, String pwd, Boolean employee)
    {
        if(employee) {
            sendObject("loginEmployee");
        } else {
            sendObject("loginCustomer");
        }

        sendObject(user);
        sendObject(pwd);
        int result = (int) receiveObject();
        return result;
    }

    /**
     * Search a subset of wines by a User
     * @param name The name of the wines
     * @param year The year of the wines
     * @return The list of filtered wines
     */
    public ArrayList<Wine> listWines(String name, String year)
    {
        sendObject("listWines");

        sendObject(name);
        sendObject(year);
        ArrayList<Wine> result = (ArrayList<Wine>) receiveObject();
        return result;
    }

    /**
     * Send a new Order by a Customer
     * @param w The Wine of the Order
     * @param c The Customer who is placing the Order
     * @param quantity The quantity of Wine bottles purchased
     * @param price The price of Wine bottles
     * @return True if the operation was successful, false otherwise
     */
    public boolean insertCustomerOrder(Wine w, Customer c, int quantity, Double price)
    {
        sendObject("insertCustomerOrder");

        sendObject(quantity);
        sendObject(price);
        sendObject(w);
        sendObject(c);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Search a subset of orders by a Customer
     * @param state The state of the orders
     * @param startDate The start date of the orders
     * @param endDate The end date of the orders
     * @param c The Customer who is searching
     * @return The list of filtered orders
     */
    public ArrayList<Order> listOrders(String state, String startDate, String endDate, Customer c)
    {
        sendObject("listOrders");

        sendObject(state);
        sendObject(startDate);
        sendObject(endDate);
        sendObject(c);
        ArrayList<Order> result = (ArrayList<Order>) receiveObject();
        return result;
    }

    /**
     * Response to an Order by a Customer
     * @param o The Order of the Customer response
     * @param state The state after the Customer choice
     * @param review The review given by the Customer
     * @return True if the operation was successful, false otherwise
     */
    public boolean responseOrderCustomer(Order o, String state, int review)
    {
        sendObject("responseOrderCustomer");

        sendObject(o);
        sendObject(state);
        sendObject(review);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Search a subset of customers by an Employee
     * @param surname The surname of the customers
     * @return The list of filtered customers
     */
    public ArrayList<Customer> listCustomers(String surname)
    {
        sendObject("listCustomers");

        sendObject(surname);
        ArrayList<Customer> result = (ArrayList<Customer>) receiveObject();
        return result;
    }

    /**
     * Search a subset of orders by an Employee
     * @param state The state of the orders
     * @param startDate The start date of the orders
     * @param endDate The end date of the orders
     * @param customersOrders True if the search is for Customer orders, false if is for Supplier orders
     * @return The list of filtered orders
     */
    public ArrayList<Order> listOrdersEmployee(String state, String startDate, String endDate, boolean customersOrders)
    {
        sendObject("listOrdersEmployee");

        sendObject(state);
        sendObject(startDate);
        sendObject(endDate);
        sendObject(customersOrders);
        ArrayList<Order> result = (ArrayList<Order>) receiveObject();
        return result;
    }

    /**
     * Check if the Wine of an Order is available
     * @param o The Order to check
     * @return The quantity of available Wine bottles
     */
    public int checkAvailabilityWine(Order o)
    {
        sendObject("checkAvailabilityWine");

        sendObject(o);
        int result = (int) receiveObject();
        return result;
    }

    /**
     * Refill the Wine in the stock
     * @param o The Order which generated the need for the refill
     * @param quantity The quantity of Wine purchased for the refill
     * @return True if the operation was successful, false otherwise
     */
    public boolean refillWine(Order o, int quantity)
    {
        sendObject("refillWine");

        sendObject(o);
        sendObject(quantity);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Send an Order to a Customer
     * @param o The Order to send
     * @param proposalDelivery The days of the proposal delivery
     * @return True if the operation was successful, false otherwise
     */
    public boolean sendOrder(Order o, int proposalDelivery)
    {
        sendObject("sendOrder");

        sendObject(o);
        sendObject(proposalDelivery);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Change password by an Employee
     * @param e The Employee whose password should be changed
     * @param password The new password
     * @return True if the operation was successful, false otherwise
     */
    public boolean changePassword(Employee e, String password)
    {
        sendObject("changePassword");

        sendObject(e);
        sendObject(password);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Check if the Employee is an admin
     * @param e The Employee to check
     * @return True if the Employee is an admin, false otherwise
     */
    public boolean checkIfAdmin(Employee e)
    {
        sendObject("checkIfAdmin");

        sendObject(e);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Add a new Employee by an Employee with admin privileges
     * @param e The Employee to add
     * @return True if the operation was successful, false otherwise
     */
    public boolean addEmployee(Employee e)
    {
        sendObject("addEmployee");

        sendObject(e);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Search a subset of employees by an Employee
     * @param username The username of the employees
     * @return The list of filtered employees
     */
    public ArrayList<Employee> listEmployees(String username)
    {
        sendObject("listEmployees");

        sendObject(username);
        ArrayList<Employee> result = (ArrayList<Employee>) receiveObject();
        return result;
    }

    /**
     * Delete an Employee by an Employee with admin privileges
     * @param e The Employee to delete
     * @return True if the operation was successful, false otherwise
     */
    public boolean deleteEmployee(Employee e)
    {
        sendObject("deleteEmployee");

        sendObject(e);
        Boolean result = (Boolean) receiveObject();
        return result;
    }

    /**
     * Generate the amount of income in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The amount of income
     */
    public Double generateIncomeCount(String startDate, String endDate)
    {
        sendObject("generateIncomeCount");

        sendObject(startDate);
        sendObject(endDate);
        Double result = (Double) receiveObject();
        return result;
    }

    /**
     * Generate the amount of costs in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The amount of costs
     */
    public Double generateCostCount(String startDate, String endDate)
    {
        sendObject("generateCostCount");

        sendObject(startDate);
        sendObject(endDate);
        Double result = (Double) receiveObject();
        return result;
    }

    /**
     * Generate the amount of bottles sold in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The amount of bottles sold
     */
    public int generateAllBottlesSold(String startDate, String endDate)
    {
        sendObject("generateAllBottlesSold");

        sendObject(startDate);
        sendObject(endDate);
        int result = (int) receiveObject();
        return result;
    }

    /**
     * Generate the amount of bottles left in a period
     * @param endDate The end date of the period to search
     * @return The amount of bottles left
     */
    public int generateAllBottlesLeft(String endDate)
    {
        sendObject("generateAllBottlesLeft");

        sendObject(endDate);
        int result = (int) receiveObject();
        return result;
    }

    /**
     * Generate the amount of bottles sold for each Wine in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The list of the amount of bottles sold for each Wine
     */
    public ArrayList<Wine> wineBottlesSold(String startDate, String endDate)
    {
        sendObject("wineBottlesSold");

        sendObject(startDate);
        sendObject(endDate);
        ArrayList<Wine> result = (ArrayList<Wine>) receiveObject();
        return result;
    }

    /**
     * Generate the amount of bottles left for each Wine in a period
     * @param endDate The end date of the period to search
     * @return The list of the amount of bottles left for each Wine
     */
    public ArrayList<Wine> wineBottlesLeft(String endDate)
    {
        sendObject("wineBottlesLeft");

        sendObject(endDate);
        ArrayList<Wine> result = (ArrayList<Wine>) receiveObject();
        return result;
    }

    /**
     * Generate the average of reviews for each Employee in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The list of the average of reviews for each Employee
     */
    public ArrayList<Employee> avgReviewEmployees(String startDate, String endDate)
    {
        sendObject("avgReviewEmployees");

        sendObject(startDate);
        sendObject(endDate);
        ArrayList<Employee> result = (ArrayList<Employee>) receiveObject();
        return result;
    }

    /**
     * Generate the amount of orders for each type and state in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The list of the amount of orders for each type and state
     */
    public ArrayList<Order> countOrderTypes(String startDate, String endDate)
    {
        sendObject("countOrderTypes");

        sendObject(startDate);
        sendObject(endDate);
        ArrayList<Order> result = (ArrayList<Order>) receiveObject();
        return result;
    }

    /**
     * Show a popup for display a message to a User
     * @param msg The message to display
     * @param warning True if the popup is a warning, flase if is an information
     */
    public void clientPopup(String msg, boolean warning) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert message;
                if(warning) {
                    message = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
                    message.setHeaderText("Warning");
                    message.setTitle("Warning");
                } else {
                    message = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
                    message.setHeaderText("Information");
                    message.setTitle("Information");
                }
                message.showAndWait();
            }
        });
    }
}
