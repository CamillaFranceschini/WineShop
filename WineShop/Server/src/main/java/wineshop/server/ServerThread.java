package wineshop.server;

import wineshop.model.*;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class for connection and communication with the clients
 */
public class ServerThread implements Runnable {
    /**
     * Server of the server thread
     */
    private Server srv;
    /**
     * Socket of the server thread
     */
    private Socket sock;
    /**
     * Input stream of the server thread
     */
    private ObjectInputStream is;
    /**
     * Output stream of the server thread
     */
    private ObjectOutputStream os;

    /**
     * Expiration days for reassign an order
     */
    private final int EXPIRATIONDAYS = 7;

    /**
     * Constructor to instantiate a new server thread
     * @param srv The server
     * @param sock The socket
     * @throws IOException
     */
    public ServerThread(final Server srv, final Socket sock) throws IOException
    {
        this.srv = srv;
        this.sock = sock;
        os = new ObjectOutputStream(new BufferedOutputStream(this.sock.getOutputStream()));
        os.flush();
        is = new ObjectInputStream(new BufferedInputStream(this.sock.getInputStream()));
    }

    /**
     * Server socket activation and handling client request
     */
    @Override
    public void run() {
        String received;
        Object oReceived;
        oReceived = receiveObject();
        if(oReceived != null) {
            received = (String) oReceived;
            System.out.println("Function called: " + received);
            switch (received) {
                case "signUp" -> sendObject(insertCustomer());
                case "loginCustomer" -> sendObject(login(false));
                case "loginEmployee" -> {
                    checkAssignationExpired();
                    sendObject(login(true));
                }
                case "listWines" -> sendObject(listWines());
                case "insertCustomerOrder" -> sendObject(insertCustomerOrder());
                case "listOrders" -> sendObject(listOrders());
                case "responseOrderCustomer" -> sendObject(responseOrderCustomer());
                case "listCustomers" -> sendObject(listCustomers());
                case "changePassword" -> sendObject(changePassword());
                case "listOrdersEmployee" -> sendObject(listOrdersEmployee());
                case "checkAvailabilityWine" -> sendObject(checkAvailabilityWine());
                case "refillWine" -> sendObject(refillWine());
                case "sendOrder" -> sendObject(sendOrder());
                case "checkIfAdmin" -> sendObject(checkIfAdmin());
                case "addEmployee" -> sendObject(addEmployee());
                case "listEmployees" -> sendObject(listEmployees());
                case "deleteEmployee" -> sendObject(deleteEmployee());
                case "generateIncomeCount" -> sendObject(generateIncomeCount());
                case "generateCostCount" -> sendObject(generateCostCount());
                case "generateAllBottlesSold" -> sendObject(generateAllBottlesSold());
                case "generateAllBottlesLeft" -> sendObject(generateAllBottlesLeft());
                case "wineBottlesSold" -> sendObject(wineBottlesSold());
                case "wineBottlesLeft" -> sendObject(wineBottlesLeft());
                case "avgReviewEmployees" -> sendObject(avgReviewEmployees());
                case "countOrderTypes" -> sendObject(countOrderTypes());
            }
        }
        closeStreams();
    }

    /**
     * Send an object to client
     * @param oSend The object to send
     */
    private boolean sendObject(Object oSend)
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
    private Object receiveObject()
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
     * Close server thread streams
     */
    public void closeStreams()
    {
        try
        {
            os.close();
            is.close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Get the date of today
     * @return The date of today
     */
    private Date getTodayDate()
    {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
        return date;
    }

    /**
     * Parse a date in the correct format for the db connection
     * @param data The date to parse
     * @param start True if the date is a start date, false if is a end date
     * @return The date parsed
     */
    private Date parsingDate(String data, boolean start)
    {
        Date parsedDate;
        try {
            if(data.isEmpty() && start) {
                parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/1970")));
            } else if(data.isEmpty() && !start) {
                parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy").parse("30/12/9999")));
            } else {
                parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("dd/MM/yyyy").parse(data)));
            }
            return parsedDate;
        } catch (ParseException ex) {
            ex.printStackTrace();
            return new Date();
        }
    }

    /**
     * Search the Employee with fewer assigned requests to assign a new one
     * @return The ID of the Employee with fewer assigned requests
     */
    private int assignEmployee() { return Database.assignEmployee(); }

    /**
     * Check if there are orders with an expired assignation date to assign them to other employees
     */
    private void checkAssignationExpired() { Database.checkAssignationExpired(EXPIRATIONDAYS); }

    /**
     * Register a new Customer
     * @return True if the operation was successful, false otherwise
     */
    private Boolean insertCustomer()
    {
        Customer c = (Customer) receiveObject();

        return Database.insertCustomer(c);
    }

    /**
     * Try login by a User
     * @param employee True if the User is an Employee, false if the User is a Customer
     * @return The unique ID of the user. -1 if the login failed
     */
    private int login(Boolean employee)
    {
        String user = (String) receiveObject();
        String pwd = (String) receiveObject();

        return Database.login(user, pwd, employee);
    }

    /**
     * Search a subset of wines by a User
     * @return The list of filtered wines
     */
    private ArrayList<Wine> listWines()
    {
        String name = (String) receiveObject();
        String year = (String) receiveObject();

        return Database.listWines(name, year);
    }

    /**
     * Send a new Order by a Customer
     * @return True if the operation was successful, false otherwise
     */
    private Boolean insertCustomerOrder()
    {
        int quantity = (int) receiveObject();
        Double price = (Double) receiveObject();
        Wine w = (Wine) receiveObject();
        Customer c = (Customer) receiveObject();

        return Database.insertOrder(quantity, price, GlobalVarAndUtilities.orderTypes.Vendita.toString(), GlobalVarAndUtilities.orderStates.Richiesto.toString(), getTodayDate(), w.getId(), assignEmployee(), c.getId());
    }

    /**
     * Search a subset of orders by a Customer
     * @return The list of filtered orders
     */
    private ArrayList<Order> listOrders()
    {
        String state = (String) receiveObject();
        String startDate = (String) receiveObject();
        String endDate = (String) receiveObject();
        Customer c = (Customer) receiveObject();

        Date sDate = parsingDate(startDate, true);
        Date eDate = parsingDate(endDate, false);

        return Database.listOrders(state, sDate, eDate, c.getId());
    }

    /**
     * Response to an Order by a Customer
     * @return True if the operation was successful, false otherwise
     */
    private Boolean responseOrderCustomer()
    {
        Order o = (Order) receiveObject();
        String state = (String) receiveObject();
        int review = (int) receiveObject();

        return Database.responseOrderCustomer(o.getIdOrder(), o.getProposalDelivery(), getTodayDate(), state, review);
    }

    /**
     * Search a subset of customers by an Employee
     * @return The list of filtered customers
     */
    private ArrayList<Customer> listCustomers()
    {
        String surname = (String) receiveObject();

        return Database.listCustomers(surname);
    }

    /**
     * Search a subset of orders by an Employee
     * @return The list of filtered orders
     */
    private ArrayList<Order> listOrdersEmployee()
    {
        String state = (String) receiveObject();
        String startDate = (String) receiveObject();
        String endDate = (String) receiveObject();
        boolean customersOrders = (boolean) receiveObject();

        Date sDate = parsingDate(startDate, true);
        Date eDate = parsingDate(endDate, false);

        if(customersOrders) {
            return Database.listOrdersEmployee(state, sDate, eDate, GlobalVarAndUtilities.orderTypes.Vendita.toString());
        } else {
            return Database.listOrdersEmployee(state, sDate, eDate, GlobalVarAndUtilities.orderTypes.Acquisto.toString());
        }
    }

    /**
     * Check if the Wine of an Order is available
     * @return The quantity of available Wine bottles
     */
    private int checkAvailabilityWine()
    {
        Order o = (Order) receiveObject();

        return Database.checkAvailabilityWine(o.getQuantity(), o.getWineName());
    }

    /**
     * Refill the Wine in the stock
     * @return True if the operation was successful, false otherwise
     */
    private boolean refillWine()
    {
        Order o = (Order) receiveObject();
        int quantity = (int) receiveObject();

        return Database.refillWine(o.getWineName(), o.getEmployeeUsername(), getTodayDate(), quantity);
    }

    /**
     * Send an Order to a Customer
     * @return True if the operation was successful, false otherwise
     */
    private boolean sendOrder()
    {
        Order o = (Order) receiveObject();
        int proposalDelivery = (int) receiveObject();

        return Database.sendOrder(o.getIdOrder(), proposalDelivery);
    }

    /**
     * Change password by an Employee
     * @return True if the operation was successful, false otherwise
     */
    private Boolean changePassword()
    {
        Employee e = (Employee) receiveObject();
        String password = (String) receiveObject();

        return Database.changePassword(e.getId(), password);
    }

    /**
     * Check if the Employee is an admin
     * @return True if the Employee is an admin, false otherwise
     */
    private Boolean checkIfAdmin()
    {
        Employee e = (Employee) receiveObject();

        return Database.checkIfAdmin(e.getId());
    }

    /**
     * Add a new Employee by an Employee with admin privileges
     * @return True if the operation was successful, false otherwise
     */
    private Boolean addEmployee()
    {
        Employee e = (Employee) receiveObject();

        return Database.addEmployee(e);
    }

    /**
     * Search a subset of employees by an Employee
     * @return The list of filtered employees
     */
    private ArrayList<Employee> listEmployees()
    {
        String username = (String) receiveObject();

        return Database.listEmployees(username);
    }

    /**
     * Delete an Employee by an Employee with admin privileges
     * @return True if the operation was successful, false otherwise
     */
    private Boolean deleteEmployee()
    {
        Employee e = (Employee) receiveObject();

        return Database.deleteEmployee(e.getUsername());
    }

    /**
     * Generate the amount of income in a period
     * @return The amount of income
     */
    private Double generateIncomeCount()
    {
        String startDate = (String) receiveObject();
        String endDate = (String) receiveObject();

        Date sDate = parsingDate(startDate, true);
        Date eDate = parsingDate(endDate, false);

        return Database.generateIncomeCount(sDate, eDate);
    }

    /**
     * Generate the amount of costs in a period
     * @return The amount of costs
     */
    private Double generateCostCount()
    {
        String startDate = (String) receiveObject();
        String endDate = (String) receiveObject();

        Date sDate = parsingDate(startDate, true);
        Date eDate = parsingDate(endDate, false);

        return Database.generateCostCount(sDate, eDate);
    }

    /**
     * Generate the amount of bottles sold in a period
     * @return The amount of bottles sold
     */
    private int generateAllBottlesSold()
    {
        String startDate = (String) receiveObject();
        String endDate = (String) receiveObject();

        Date sDate = parsingDate(startDate, true);
        Date eDate = parsingDate(endDate, false);

        return Database.generateAllBottlesSold(sDate, eDate);
    }

    /**
     * Generate the amount of bottles left in a period
     * @return The amount of bottles left
     */
    private int generateAllBottlesLeft()
    {
        String endDate = (String) receiveObject();

        Date eDate = parsingDate(endDate, false);

        return Database.generateAllBottlesLeft(eDate);
    }

    /**
     * Generate the amount of bottles sold for each Wine in a period
     * @return The list of the amount of bottles sold for each Wine
     */
    private ArrayList<Wine> wineBottlesSold()
    {
        String startDate = (String) receiveObject();
        String endDate = (String) receiveObject();

        Date sDate = parsingDate(startDate, true);
        Date eDate = parsingDate(endDate, false);

        return Database.wineBottlesSold(sDate, eDate);
    }

    /**
     * Generate the amount of bottles left for each Wine in a period
     * @return The list of the amount of bottles left for each Wine
     */
    private ArrayList<Wine> wineBottlesLeft()
    {
        String endDate = (String) receiveObject();

        Date eDate = parsingDate(endDate, false);

        return Database.wineBottlesLeft(eDate);
    }

    /**
     * Generate the average of reviews for each Employee in a period
     * @return The list of the average of reviews for each Employee
     */
    private ArrayList<Employee> avgReviewEmployees()
    {
        String startDate = (String) receiveObject();
        String endDate = (String) receiveObject();

        Date sDate = parsingDate(startDate, true);
        Date eDate = parsingDate(endDate, false);

        return Database.avgReviewEmployees(sDate, eDate);
    }

    /**
     * Generate the amount of orders for each type and state in a period
     * @return The list of the amount of orders for each type and state
     */
    private ArrayList<Order> countOrderTypes()
    {
        String startDate = (String) receiveObject();
        String endDate = (String) receiveObject();

        Date sDate = parsingDate(startDate, true);
        Date eDate = parsingDate(endDate, false);

        return Database.countOrderTypes(sDate, eDate);
    }
}
