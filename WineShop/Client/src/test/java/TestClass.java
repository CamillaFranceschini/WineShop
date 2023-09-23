import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wineshop.client.Client;
import wineshop.model.*;

import java.util.ArrayList;

/**
 * Test Class
 * @author Mirko Piazza, Camilla Franceschini
 */
public class TestClass {
    /**
     * Client for server connection
     */
    Client client;

    /**
     * Start the client connection for the test
     */
    @BeforeEach
    void startTest()
    {
        client = new Client();
    }

    /**
     * Close the client connection for the test
     */
    @AfterEach
    void endTest()
    {
        client.closeConnection();
    }

    /**
     * Unit test of signUp method
     * Disabled for avoid changes to application data
     */
    @Test
    @Disabled
    final void testSignUp()
    {
        assertTrue(client.signUp(new Customer("test", "test", "AAAAAA00A00A000A", "test@test.com", "1234567890", "via test 1", "test", "password")));
    }

    /**
     * Unit test of login method for Customer
     */
    @Test
    final void testLoginCustomer()
    {
        assertEquals(1, client.login("mpiazza", "password", false));
    }

    /**
     * Unit test of login method for Employee
     */
    @Test
    final void testLoginEmployee()
    {
        assertEquals(1, client.login("mcostanzo", "password", true));
    }

    /**
     * Unit test of listWines method
     */
    @Test
    final void testListWines()
    {
        assertNotEquals(new ArrayList<Wine>(), client.listWines("",""));
    }

    /**
     * Unit test of listOrders method
     */
    @Test
    final void testListOrders()
    {
        assertNotEquals(new ArrayList<Order>(), client.listOrders(GlobalVarAndUtilities.orderStates.Completato.toString(), "", "", new Customer(1,"mpiazza")));
    }

    /**
     * Unit test of listCustomers method
     */
    @Test
    final void testListCustomers()
    {
        assertNotEquals(new ArrayList<Customer>(), client.listCustomers(""));
    }

    /**
     * Unit test of listOrdersEmployee method for customers orders
     */
    @Test
    final void testListOrdersEmployeeCustomers()
    {
        assertNotEquals(new ArrayList<Order>(), client.listOrdersEmployee(GlobalVarAndUtilities.orderStates.Completato.toString(), "", "", true));
    }

    /**
     * Unit test of listOrdersEmployee method for suppliers orders
     */
    @Test
    final void testListOrdersEmployeeSuppliers()
    {
        assertNotEquals(new ArrayList<Order>(), client.listOrdersEmployee(GlobalVarAndUtilities.orderStates.Completato.toString(), "", "", false));
    }

    /**
     * Unit test of checkAvailabilityWine method
     */
    @Test
    final void testCheckAvailabilityWine()
    {
        assertNotEquals(-999, client.checkAvailabilityWine(new Order(11, 1, null, null, null, null, null, 3, null, 5, "Sassicaia", null, null)));
    }

    /**
     * Unit test of checkIfAdmin method for an admin
     */
    @Test
    final void testCheckIfAdminAdmin()
    {
        assertTrue(client.checkIfAdmin(new Employee(1, "mcostanzo")));
    }

    /**
     * Unit test of checkIfAdmin method for a non-admin
     */
    @Test
    final void testCheckIfAdminNoAdmin()
    {
        assertFalse(client.checkIfAdmin(new Employee(2, "ccamillini")));
    }

    /**
     * Unit test of listEmployees method
     */
    @Test
    final void testListEmployees()
    {
        assertNotEquals(new ArrayList<Employee>(), client.listEmployees(""));
    }

    /**
     * Unit test of generateIncomeCount method
     */
    @Test
    final void testGenerateIncomeCount()
    {
        assertNotEquals(0.0, client.generateIncomeCount("", ""));
    }

    /**
     * Unit test of generateCostCount method
     */
    @Test
    final void testGenerateCostCount()
    {
        assertNotEquals(0.0, client.generateCostCount("", ""));
    }

    /**
     * Unit test of generateAllBottlesSold method
     */
    @Test
    final void testGenerateAllBottlesSold()
    {
        assertNotEquals(0, client.generateAllBottlesSold("", ""));
    }

    /**
     * Unit test of generateAllBottlesLeft method
     */
    @Test
    final void testGenerateAllBottlesLeft()
    {
        assertNotEquals(0, client.generateAllBottlesLeft(""));
    }

    /**
     * Unit test of wineBottlesSold method
     */
    @Test
    final void testWineBottlesSold()
    {
        assertNotEquals(new ArrayList<Wine>(), client.wineBottlesSold("", ""));
    }

    /**
     * Unit test of wineBottlesLeft method
     */
    @Test
    final void testWineBottlesLeft()
    {
        assertNotEquals(new ArrayList<Wine>(), client.wineBottlesLeft(""));
    }

    /**
     * Unit test of avgReviewEmployees method
     */
    @Test
    final void testAvgReviewEmployees()
    {
        assertNotEquals(new ArrayList<Employee>(), client.avgReviewEmployees("", ""));
    }

    /**
     * Unit test of countOrderTypes method
     */
    @Test
    final void testCountOrderTypes()
    {
        assertNotEquals(new ArrayList<Order>(), client.countOrderTypes("", ""));
    }

    /**
     * Integration test of addEmployee, changePassword and deleteEmployee methods
     */
    @Test
    final void testEmployeeChanges()
    {
        assertTrue(client.addEmployee(new Employee("test", "test", "AAAAAA00A00A000A", "test@test.com", "1234567890", "via test 1", "test", "password", false)));
        startTest();
        assertTrue(client.changePassword(new Employee("test", "test", "AAAAAA00A00A000A", "test@test.com", "1234567890", "via test 1", "test", "password", false), "new_password"));
        endTest();
        startTest();
        assertTrue(client.deleteEmployee(new Employee("test", "test", "AAAAAA00A00A000A", "test@test.com", "1234567890", "via test 1", "test", "password", false)));
    }

    /**
     * Integration test of insertCustomerOrder, refillWine, sendOrder and responseOrderCustomer methods
     * Disabled for avoid changes to application data
     */
    @Test
    @Disabled
    final void testOrderFlow()
    {
        assertTrue(client.insertCustomerOrder(new Wine(1, null, null, null, 2017, null, null, null, null, 6, null, null), new Customer(1, "mpiazza"), 1, 250.0));
        endTest();
        startTest();
        assertTrue(client.refillWine(new Order(18, 1, null, null, null, null, null, 3, null, 5, "Sassicaia", "ccamillini", null), 1));
        endTest();
        startTest();
        assertTrue(client.sendOrder(new Order(18, 1, null, null, null, null, null, 3, null, 5, null, null, null), 3));
        endTest();
        startTest();
        assertTrue(client.responseOrderCustomer(new Order(18, 1, null, null, null, null, null, 3, null, 5, null, null, null), GlobalVarAndUtilities.orderStates.Completato.toString(), 5));
    }
}