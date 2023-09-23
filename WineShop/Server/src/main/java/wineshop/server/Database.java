package wineshop.server;

import wineshop.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Class for db connection
 * @author Mirko Piazza, Camilla Franceschini
 */
public class Database {
    /**
     * DB's location
     */
    private static final String DB_Location = "localhost";
    /**
     * DB's port
     */
    private static final String DB_Port = "3306";
    /**
     * JDBC string for DB connection
     */
    private static final String JDBC_STRING = "jdbc:mysql://" + DB_Location + ":" + DB_Port + "/wineshop";
    /**
     * User for DB login
     */
    private static final String LOGIN = "root";
    /**
     * Password for DB login
     */
    private static final String PASSWORD = "";

    /**
     * Statement for DB queries
     */
    private static Statement stmt;
    /**
     * Connection for DB queries
     */
    private static Connection conn;

    /**
     * Milliseconds in a day
     */
    private static final int MILLISECONDSDAY = 86400000;

    /**
     * Activation of DB connection
     */
    static
    {
        try
        {
            conn = DriverManager.getConnection(JDBC_STRING, LOGIN, PASSWORD);
            stmt = conn.createStatement();
        }
        catch (SQLException ex)
        {
            stmt = null;
            ex.printStackTrace();
        }
    }

    /**
     * Search the Employee with fewer assigned requests to assign a new one
     * @return The ID of the Employee with fewer assigned requests
     */
    public static int assignEmployee()
    {
        String query = "SELECT e.id as id, count(i.id_order) as jobs FROM employee e LEFT JOIN itemledgerentry i ON e.id = i.id_employee AND i.state = \"" + GlobalVarAndUtilities.orderStates.Richiesto.toString() + "\" WHERE e.admin = false GROUP BY e.id ORDER BY jobs, rand()";
        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            rset = pstmt.executeQuery();
            rset.next();
            return rset.getInt("id");
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * Search the Employee with fewer assigned requests to assign a new one. Excludes from the pool the employee ID received in input
     * @param id_old_employee The ID of Employee to exclude from the pool
     * @return The ID of the Employee with fewer assigned requests
     */
    public static int assignEmployee(int id_old_employee)
    {
        String query = "SELECT e.id as id, count(i.id_order) as jobs FROM employee e LEFT JOIN itemledgerentry i ON e.id = i.id_employee AND i.state = \"" + GlobalVarAndUtilities.orderStates.Richiesto.toString() + "\" WHERE e.admin = false AND i.id_employee != ? GROUP BY e.id ORDER BY jobs, rand()";
        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id_old_employee);

            rset = pstmt.executeQuery();
            rset.next();
            return rset.getInt("id");
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * Check if there are orders with an expired assignation date to assign them to other employees
     * @param expiration The number of days to calculate the expiration
     */
    public static void checkAssignationExpired(int expiration)
    {
        String query1 = "UPDATE itemLedgerEntry SET state = \"" + GlobalVarAndUtilities.orderStates.Scaduto.toString() + "\" WHERE state = \"" + GlobalVarAndUtilities.orderStates.Richiesto.toString() + "\" AND id_order = ? AND id_employee = ?";
        String query2 = "INSERT INTO itemLedgerEntry (id_order, quantity, price, type, state, order_date, assignation_date, proposal_delivery, delivery_date, review_employee, id_wine, id_employee, id_customer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Order o;
        ArrayList<Order> orderList = new ArrayList<Order>();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        int i = 0;

        try
        {
            String query_temp = "SELECT id_order, quantity, price, type, state, order_date, assignation_date, proposal_delivery, delivery_date, review_employee, id_wine, id_employee, id_customer FROM itemledgerentry WHERE type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" AND state = \"" + GlobalVarAndUtilities.orderStates.Richiesto.toString() + "\" AND assignation_date < (now() - INTERVAL ? DAY)";
            pstmt = conn.prepareStatement(query_temp);
            pstmt.setInt(1, expiration);
            rset = pstmt.executeQuery();
            while (rset.next())
            {
                o = new Order(rset.getInt("id_order"), rset.getInt("quantity"), rset.getDouble("price"), rset.getString("type"), rset.getString("state"), new Date(rset.getTimestamp("order_date").getTime()), new Date(rset.getTimestamp("assignation_date").getTime()), rset.getInt("proposal_delivery"), null, rset.getInt("review_employee"), null, null, null);
                ids.add(rset.getInt("id_wine"));
                ids.add(rset.getInt("id_employee"));
                ids.add(rset.getInt("id_customer"));
                orderList.add(o);
            }

            //Auto commit deactivation due to the 2 manipulation queries to be executed
            conn.setAutoCommit(false);
            for(Order ord : orderList)
            {
                pstmt = conn.prepareStatement(query1);
                pstmt.setInt(1, ord.getIdOrder());
                pstmt.setInt(2, ids.get(i+1));
                pstmt.execute();

                pstmt = conn.prepareStatement(query2);
                pstmt.setInt(1, ord.getIdOrder());
                pstmt.setInt(2, ord.getQuantity());
                pstmt.setDouble(3, ord.getPrice());
                pstmt.setString(4, ord.getType());
                pstmt.setString(5, ord.getState());
                pstmt.setTimestamp(6, new Timestamp(ord.getOrderDate().getTime()));
                pstmt.setTimestamp(7, new Timestamp(new Date().getTime()));
                pstmt.setNull(8, Types.INTEGER);
                pstmt.setNull(9, Types.TIMESTAMP);
                pstmt.setNull(10, Types.INTEGER);
                pstmt.setInt(11, ids.get(i));
                pstmt.setInt(12, assignEmployee(ids.get(i+1)));
                pstmt.setInt(13, ids.get(i+2));
                pstmt.execute();
                conn.commit();
                i += 3;
            }
            conn.setAutoCommit(true);
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                return;
            } catch (SQLException ex2) {
                return;
            }
        }
    }

    /**
     * Register a new Customer
     * @param c The new Customer
     * @return True if the operation was successful, false otherwise
     */
    public static boolean insertCustomer(Customer c)
    {
        String query = "INSERT INTO customer (name, surname, cf, email, phone, address, username, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getSurname());
            pstmt.setString(3, c.getCf());
            pstmt.setString(4, c.getEmail());
            pstmt.setString(5, c.getPhone());
            pstmt.setString(6, c.getAddress());
            pstmt.setString(7, c.getUsername());
            pstmt.setString(8, c.getPassword());

            pstmt.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Try login by a User
     * @param user The username of the User
     * @param pwd The password of the User
     * @param employee True if the User is an Employee, false if the User is a Customer
     * @return The unique ID of the user. -1 if the login failed
     */
    public static int login(String user, String pwd, Boolean employee)
    {
        String query;

        if (employee) {
            query = "SELECT id FROM employee WHERE username = ? AND password = ?";
        }
        else {
            query = "SELECT id FROM customer WHERE username = ? AND password = ?";
        }

        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, user);
            pstmt.setString(2, pwd);

            rset = pstmt.executeQuery();
            if(rset.next()) {
                return rset.getInt("id");
            } else { return -1; }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * Search a subset of wines by a User
     * @param name The name of the wines
     * @param year The year of the wines
     * @return The list of filtered wines
     */
    public static ArrayList<Wine> listWines(String name, String year)
    {
        String query = "SELECT w.id as id, w.name as name, producer, origin, year, technical_notes, vines, vines, sale_price, purchase_price, threshold, c.name as courier_name, s.name as supplier_name FROM wine w, courier c, supplier s WHERE w.id_courier = c.id AND w.id_supplier = s.id AND w.name like ? AND year like ?";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Wine w;
        ArrayList<Wine> wineList = new ArrayList<Wine>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, "%" + name + "%");
            pstmt.setString(2, "%" + year + "%");

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                w = new Wine(rset.getInt("id"), rset.getString("name"), rset.getString("producer"), rset.getString("origin"), rset.getInt("year"), rset.getString("technical_notes"), rset.getString("vines"), rset.getDouble("sale_price"), rset.getDouble("purchase_price"), rset.getInt("threshold"), rset.getString("courier_name"), rset.getString("supplier_name"));
                wineList.add(w);
            }
            return wineList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Wine>();
        }
    }

    /**
     * Send a new Order by a Customer
     * @param quantity The quantity of Wine bottles purchased
     * @param price The price of Wine bottles
     * @param type The type of the Order
     * @param state The state of the Order
     * @param date The date of the order
     * @param id_wine The wine ID of the Order
     * @param id_employee The employee ID of the Order
     * @param id_customer The customer ID of the Order
     * @return True if the operation was successful, false otherwise
     */
    public static boolean insertOrder(int quantity, Double price, String type, String state, Date date, int id_wine, int id_employee, int id_customer)
    {
        String query = "INSERT INTO itemLedgerEntry (id_order, quantity, price, type, state, order_date, assignation_date, proposal_delivery, delivery_date, review_employee, id_wine, id_employee, id_customer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt;
        ResultSet rset = null;
        int id_order;

        try
        {
            String query_temp = "SELECT max(id_order) as max_id_order FROM itemLedgerEntry";
            pstmt = conn.prepareStatement(query_temp);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                id_order = Integer.valueOf(rset.getString("max_id_order")) + 1;
            } else { return false; }

            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id_order);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, price);
            pstmt.setString(4, type);
            pstmt.setString(5, state);
            pstmt.setTimestamp(6, new Timestamp(date.getTime()));
            pstmt.setTimestamp(7, new Timestamp(date.getTime()));
            pstmt.setNull(8, Types.INTEGER);
            pstmt.setNull(9, Types.TIMESTAMP);
            pstmt.setNull(10, Types.INTEGER);
            pstmt.setInt(11, id_wine);
            pstmt.setInt(12, id_employee);
            pstmt.setInt(13, id_customer);

            pstmt.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Search a subset of orders by a Customer
     * @param state The state of the orders
     * @param startDate The start date of the orders
     * @param endDate The end date of the orders
     * @param id_customer The customer ID who is searching
     * @return The list of filtered orders
     */
    public static ArrayList<Order> listOrders(String state, Date startDate, Date endDate, int id_customer)
    {
        String query = "SELECT id_order, quantity, price, type, state, order_date, proposal_delivery, delivery_date, assignation_date, review_employee, w.name as wine_name, e.username as employee_username, c.username as customer_username FROM itemledgerentry i, wine w, customer c, employee e WHERE i.id_wine = w.id AND i.id_customer = c.id AND i.id_employee = e.id  AND id_customer = ? AND type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" AND state = ? AND order_date >= ? AND order_date <= ?";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Order o;
        ArrayList<Order> orderList = new ArrayList<Order>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id_customer);
            pstmt.setString(2, state);
            pstmt.setDate(3, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(4, new java.sql.Date(endDate.getTime()));

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                Date deliveryDate = new Date();
                if(rset.getTimestamp("delivery_date") != null) { deliveryDate = new Date(rset.getTimestamp("delivery_date").getTime()); }
                o = new Order(rset.getInt("id_order"), rset.getInt("quantity"), rset.getDouble("price"), rset.getString("type"), rset.getString("state"), new Date(rset.getTimestamp("order_date").getTime()), new Date(rset.getTimestamp("assignation_date").getTime()), rset.getInt("proposal_delivery"), deliveryDate, rset.getInt("review_employee"), rset.getString("wine_name"), rset.getString("employee_username"), rset.getString("customer_username"));
                orderList.add(o);
            }
            return orderList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Order>();
        }
    }

    /**
     * Response to an Order by a Customer
     * @param id_order The order ID of the Order
     * @param proposal_delivery The proposal delivery of the Order
     * @param date The date of the Order
     * @param state The state after the Customer choice
     * @param review The review given by the Customer
     * @return True if the operation was successful, false otherwise
     */
    public static boolean responseOrderCustomer(int id_order, int proposal_delivery, Date date, String state, int review)
    {
        String query = "UPDATE itemLedgerEntry SET state = ?, review_employee = ?, delivery_date = ? WHERE state = \"" + GlobalVarAndUtilities.orderStates.ConfermaUtente.toString() + "\" AND id_order = ?";
        PreparedStatement pstmt;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, state);
            if(Objects.equals(state, GlobalVarAndUtilities.orderStates.Completato.toString()))
            {
                pstmt.setInt(2, review);
                pstmt.setTimestamp(3, new Timestamp(date.getTime() + proposal_delivery * MILLISECONDSDAY));
            } else {
                pstmt.setNull(2, Types.INTEGER);
                pstmt.setNull(3, Types.TIMESTAMP);
            }
            pstmt.setInt(4, id_order);

            pstmt.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Search a subset of customers by an Employee
     * @param surname The surname of the customers
     * @return The list of filtered customers
     */
    public static ArrayList<Customer> listCustomers(String surname)
    {
        String query = "SELECT id, name, surname, cf, email, phone, address, username, password FROM customer WHERE surname like ?";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Customer c;
        ArrayList<Customer> customerList = new ArrayList<Customer>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, "%" + surname + "%");

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                c = new Customer(rset.getString("name"), rset.getString("surname"), rset.getString("cf"), rset.getString("email"), rset.getString("phone"), rset.getString("address"), rset.getString("username"), rset.getString("password"));
                customerList.add(c);
            }
            return customerList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Customer>();
        }
    }

    /**
     * Search a subset of orders by an Employee
     * @param state The state of the orders
     * @param startDate The start date of the orders
     * @param endDate The end date of the orders
     * @param type The type of the orders
     * @return The list of filtered orders
     */
    public static ArrayList<Order> listOrdersEmployee(String state, Date startDate, Date endDate, String type)
    {
        String query;

        if (type == GlobalVarAndUtilities.orderTypes.Vendita.toString()) {
            query = "SELECT id_order, quantity, price, type, state, order_date, proposal_delivery, delivery_date, assignation_date, review_employee, w.name as wine_name, e.username as employee_username, c.username as customer_username FROM itemledgerentry i, wine w, customer c, employee e WHERE i.id_wine = w.id AND i.id_customer = c.id AND i.id_employee = e.id AND type = ? AND state = ? AND order_date >= ? AND order_date <= ?";
        }
        else {
            query = "SELECT id_order, quantity, price, type, state, order_date, proposal_delivery, delivery_date, assignation_date, review_employee, w.name as wine_name, e.username as employee_username FROM itemledgerentry i, wine w, employee e WHERE i.id_wine = w.id AND i.id_employee = e.id AND type = ? AND state = ? AND order_date >= ? AND order_date <= ?";
        }

        PreparedStatement pstmt;
        ResultSet rset = null;
        Order o;
        ArrayList<Order> orderList = new ArrayList<Order>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, type);
            pstmt.setString(2, state);
            pstmt.setDate(3, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(4, new java.sql.Date(endDate.getTime()));

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                Date deliveryDate = new Date();
                String customerUsername = "";
                if(rset.getTimestamp("delivery_date") != null) { deliveryDate = new Date(rset.getTimestamp("delivery_date").getTime()); }
                if(type == GlobalVarAndUtilities.orderTypes.Vendita.toString()) { customerUsername = rset.getString("customer_username"); }
                o = new Order(rset.getInt("id_order"), rset.getInt("quantity"), rset.getDouble("price"), rset.getString("type"), rset.getString("state"), new Date(rset.getTimestamp("order_date").getTime()), new Date(rset.getTimestamp("assignation_date").getTime()), rset.getInt("proposal_delivery"), deliveryDate, rset.getInt("review_employee"), rset.getString("wine_name"), rset.getString("employee_username"), customerUsername);
                orderList.add(o);
            }
            return orderList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Order>();
        }
    }

    /**
     * Check if the Wine of an Order is available
     * @param quantity The quantity requested from the Customer
     * @param wine_name The wine name of the Order
     * @return The quantity of available Wine bottles
     */
    public static int checkAvailabilityWine(int quantity, String wine_name)
    {
        String query = "SELECT w.name, (sum(case when type = \"" + GlobalVarAndUtilities.orderTypes.Acquisto.toString() + "\" then quantity when type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" then - quantity end) - w.threshold - ?) as stock FROM itemledgerentry i, wine w WHERE i.id_wine = w.id AND (state =\"" + GlobalVarAndUtilities.orderStates.Completato.toString() + "\" or state =\"" + GlobalVarAndUtilities.orderStates.ConfermaUtente.toString() + "\") AND w.name = ?";
        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, quantity);
            pstmt.setString(2, wine_name);

            rset = pstmt.executeQuery();
            if(rset.next()) {
                return rset.getInt("stock");
            } else { return -999; }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return -999;
        }
    }

    /**
     * Refill the Wine in the stock
     * @param wine_name The wine name of the Order
     * @param employee_username The employee username of the Order
     * @param date The date of the Order
     * @param quantity The quantity of Wine purchased for the refill
     * @return True if the operation was successful, false otherwise
     */
    public static boolean refillWine(String wine_name, String employee_username, Date date, int quantity)
    {
        String query = "INSERT INTO itemLedgerEntry (id_order, quantity, price, type, state, order_date, assignation_date, proposal_delivery, delivery_date, review_employee, id_wine, id_employee, id_customer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt;
        ResultSet rset = null;
        int id_order;
        Double price;
        int id_wine;
        int id_employee;

        try
        {
            String query_temp;
            query_temp = "SELECT max(id_order) as max_id_order FROM itemLedgerEntry";
            pstmt = conn.prepareStatement(query_temp);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                id_order = Integer.valueOf(rset.getString("max_id_order")) + 1;
            } else { return false; }

            query_temp = "SELECT id, purchase_price FROM wine WHERE name = ?";
            pstmt = conn.prepareStatement(query_temp);
            pstmt.setString(1, wine_name);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                id_wine = Integer.valueOf(rset.getString("id")) ;
                price = Double.valueOf(rset.getString("purchase_price"));
            } else { return false; }

            query_temp = "SELECT id FROM employee WHERE username = ?";
            pstmt = conn.prepareStatement(query_temp);
            pstmt.setString(1, employee_username);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                id_employee = rset.getInt("id");
            } else { return false; }

            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id_order);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, price*quantity);
            pstmt.setString(4, GlobalVarAndUtilities.orderTypes.Acquisto.toString());
            pstmt.setString(5, GlobalVarAndUtilities.orderStates.Completato.toString());
            pstmt.setTimestamp(6, new Timestamp(date.getTime()));
            pstmt.setTimestamp(7, new Timestamp(date.getTime()));
            pstmt.setNull(8, Types.INTEGER);
            pstmt.setTimestamp(9, new Timestamp(date.getTime()));
            pstmt.setNull(10, Types.INTEGER);
            pstmt.setInt(11, id_wine);
            pstmt.setInt(12, id_employee);
            pstmt.setNull(13, Types.INTEGER);

            pstmt.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Send an Order to a Customer
     * @param id_order The order ID to send
     * @param proposal_delivery The days of the proposal delivery
     * @return True if the operation was successful, false otherwise
     */
    public static boolean sendOrder(int id_order, int proposal_delivery)
    {
        String query = "UPDATE itemLedgerEntry SET state =\"" + GlobalVarAndUtilities.orderStates.ConfermaUtente.toString() + "\", proposal_delivery = ? WHERE state = \"" + GlobalVarAndUtilities.orderStates.Richiesto.toString() + "\" AND id_order = ?";
        PreparedStatement pstmt;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, proposal_delivery);
            pstmt.setInt(2, id_order);

            pstmt.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Change password by an Employee
     * @param id_employee The employee ID whose password should be changed
     * @param password The new password
     * @return True if the operation was successful, false otherwise
     */
    public static boolean changePassword(int id_employee, String password)
    {
        String query = "UPDATE employee SET password = ? WHERE id = ?";
        PreparedStatement pstmt;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, password);
            pstmt.setInt(2, id_employee);

            pstmt.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Check if the Employee is an admin
     * @param id_employee The employee ID to check
     * @return True if the Employee is an admin, false otherwise
     */
    public static boolean checkIfAdmin(int id_employee)
    {
        String query = "SELECT id FROM employee WHERE id = ? AND admin = true";
        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id_employee);

            rset = pstmt.executeQuery();
            if(rset.next()) {
                return true;
            } else { return false; }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Add a new Employee by an Employee with admin privileges
     * @param e The Employee to add
     * @return True if the operation was successful, false otherwise
     */
    public static boolean addEmployee(Employee e)
    {
        String query = "INSERT INTO employee (name, surname, cf, email, phone, address, username, password, admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, e.getName());
            pstmt.setString(2, e.getSurname());
            pstmt.setString(3, e.getCf());
            pstmt.setString(4, e.getEmail());
            pstmt.setString(5, e.getPhone());
            pstmt.setString(6, e.getAddress());
            pstmt.setString(7, e.getUsername());
            pstmt.setString(8, e.getPassword());
            pstmt.setBoolean(9, e.getAdmin());

            pstmt.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Search a subset of employees by an Employee
     * @param username The username of the employees
     * @return The list of filtered employees
     */
    public static ArrayList<Employee> listEmployees(String username)
    {
        String query = "SELECT id, name, surname, cf, email, phone, address, username, password, admin FROM employee WHERE username like ?";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Employee e;
        ArrayList<Employee> employeeList = new ArrayList<Employee>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, "%" + username + "%");

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                e = new Employee(rset.getString("name"), rset.getString("surname"), rset.getString("cf"), rset.getString("email"), rset.getString("phone"), rset.getString("address"), rset.getString("username"), rset.getString("password"), rset.getBoolean("admin"));
                employeeList.add(e);
            }
            return employeeList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Employee>();
        }
    }

    /**
     * Delete an Employee by an Employee with admin privileges
     * @param username The employee username to delete
     * @return True if the operation was successful, false otherwise
     */
    public static boolean deleteEmployee(String username)
    {
        String query = "DELETE FROM employee WHERE username = ?";
        PreparedStatement pstmt;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, username);

            pstmt.execute();
            return true;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Generate the amount of income in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The amount of income
     */
    public static Double generateIncomeCount(Date startDate, Date endDate)
    {
        String query = "SELECT sum(price) as price FROM itemledgerentry WHERE type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" AND state = \"" + GlobalVarAndUtilities.orderStates.Completato.toString() + "\" AND order_date >= ? AND order_date < ?";
        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()+MILLISECONDSDAY));

            rset = pstmt.executeQuery();
            if(rset.next()) {
                return rset.getDouble("price");
            } else { return 0.0; }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Generate the amount of costs in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The amount of costs
     */
    public static Double generateCostCount(Date startDate, Date endDate)
    {
        String query = "SELECT sum(price) as price FROM itemledgerentry WHERE type = \"" + GlobalVarAndUtilities.orderTypes.Acquisto.toString() + "\" AND state = \"" + GlobalVarAndUtilities.orderStates.Completato.toString() + "\" AND order_date >= ? AND order_date < ?";
        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()+MILLISECONDSDAY));

            rset = pstmt.executeQuery();
            if(rset.next()) {
                return rset.getDouble("price");
            } else { return 0.0; }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Generate the amount of bottles sold in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The amount of bottles sold
     */
    public static int generateAllBottlesSold(Date startDate, Date endDate)
    {
        String query = "SELECT sum(quantity) as quantity FROM itemledgerentry WHERE type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" AND state = \"" + GlobalVarAndUtilities.orderStates.Completato.toString() + "\" AND order_date >= ? AND order_date < ?";
        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()+MILLISECONDSDAY));

            rset = pstmt.executeQuery();
            if(rset.next()) {
                return rset.getInt("quantity");
            } else { return 0; }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Generate the amount of bottles left in a period
     * @param endDate The end date of the period to search
     * @return The amount of bottles left
     */
    public static int generateAllBottlesLeft(Date endDate)
    {
        String query = "SELECT sum(case when type = \"" + GlobalVarAndUtilities.orderTypes.Acquisto.toString() + "\" then quantity when type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" then - quantity end) as magazzino FROM itemledgerentry WHERE state = \"" + GlobalVarAndUtilities.orderStates.Completato.toString() + "\" AND order_date < ?";
        PreparedStatement pstmt;
        ResultSet rset = null;

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setTimestamp(1, new Timestamp(endDate.getTime()+MILLISECONDSDAY));

            rset = pstmt.executeQuery();
            if(rset.next()) {
                return rset.getInt("magazzino");
            } else { return 0; }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Generate the amount of bottles sold for each Wine in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The list of the amount of bottles sold for each Wine
     */
    public static ArrayList<Wine> wineBottlesSold(Date startDate, Date endDate)
    {
        String query = "SELECT w.name as name, sum(quantity) as quantity FROM itemledgerentry i, wine w WHERE i.id_wine = w.id AND state = \"" + GlobalVarAndUtilities.orderStates.Completato.toString() + "\" AND type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" AND order_date >= ? AND order_date < ? GROUP BY w.id";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Wine w;
        ArrayList<Wine> wineList = new ArrayList<Wine>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()+MILLISECONDSDAY));

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                w = new Wine(rset.getString("name"), rset.getInt("quantity"));
                wineList.add(w);
            }
            return wineList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Wine>();
        }
    }

    /**
     * Generate the amount of bottles left for each Wine in a period
     * @param endDate The end date of the period to search
     * @return The list of the amount of bottles left for each Wine
     */
    public static ArrayList<Wine> wineBottlesLeft(Date endDate)
    {
        String query = "SELECT w.name as name, sum(case when type = \"" + GlobalVarAndUtilities.orderTypes.Acquisto.toString() + "\" then quantity when type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" then - quantity end) as magazzino FROM itemledgerentry i, wine w WHERE i.id_wine = w.id AND state = \"" + GlobalVarAndUtilities.orderStates.Completato.toString() + "\" AND order_date < ? GROUP BY w.id";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Wine w;
        ArrayList<Wine> wineList = new ArrayList<Wine>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setTimestamp(1, new Timestamp(endDate.getTime()+MILLISECONDSDAY));

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                w = new Wine(rset.getString("name"), rset.getInt("magazzino"));
                wineList.add(w);
            }
            return wineList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Wine>();
        }
    }

    /**
     * Generate the average of reviews for each Employee in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The list of the average of reviews for each Employee
     */
    public static ArrayList<Employee> avgReviewEmployees(Date startDate, Date endDate)
    {
        String query = "SELECT e.username as username, avg(review_employee) as review_employee FROM itemledgerentry i, employee e WHERE i.id_employee = e.id AND type = \"" + GlobalVarAndUtilities.orderTypes.Vendita.toString() + "\" AND review_employee is not null AND order_date >= ? AND order_date < ? GROUP BY e.id";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Employee e;
        ArrayList<Employee> employeeList = new ArrayList<Employee>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()+MILLISECONDSDAY));

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                e = new Employee(rset.getString("username"), rset.getDouble("review_employee"));
                employeeList.add(e);
            }
            return employeeList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Employee>();
        }
    }

    /**
     * Generate the amount of orders for each type and state in a period
     * @param startDate The start date of the period to search
     * @param endDate The end date of the period to search
     * @return The list of the amount of orders for each type and state
     */
    public static ArrayList<Order> countOrderTypes(Date startDate, Date endDate)
    {
        String query = "SELECT type, state, count(*) as count FROM itemledgerentry WHERE order_date >= ? AND order_date < ? GROUP BY type, state";
        PreparedStatement pstmt;
        ResultSet rset = null;
        Order o;
        ArrayList<Order> orderList = new ArrayList<Order>();

        try
        {
            pstmt = conn.prepareStatement(query);

            pstmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(2, new Timestamp(endDate.getTime()+MILLISECONDSDAY));

            rset = pstmt.executeQuery();

            while (rset.next())
            {
                o = new Order(rset.getString("type"), rset.getString("state"), rset.getInt("count"));
                orderList.add(o);
            }
            return orderList;
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            return new ArrayList<Order>();
        }
    }
}
