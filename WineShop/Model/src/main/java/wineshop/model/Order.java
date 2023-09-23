package wineshop.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for represent information about an Order
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Order ID of the Order
     */
    private int idOrder;
    /**
     * Quantity of the Order
     */
    private int quantity;
    /**
     * Price of the Order
     */
    private Double price;
    /**
     * Type of the Order
     */
    private String type;
    /**
     * State of the Order
     */
    private String state;
    /**
     * Date of the Order
     */
    private Date orderDate;
    /**
     * Assignation date of the Order
     */
    private Date assignationDate;
    /**
     * Proposal delivery of the Order
     */
    private int proposalDelivery;
    /**
     * Delivery date of the Order
     */
    private Date deliveryDate;
    /**
     * Review of the Order
     */
    private int reviewEmployee;
    /**
     * Wine name of the Order
     */
    private String wineName;
    /**
     * Employee username of the Order
     */
    private String employeeUsername;
    /**
     * Customer username of the Order
     */
    private String customerUsername;

    /**
     * Constructor to instantiate a Order
     * @param idOrder The order ID of the Order
     * @param quantity The quantity of the Order
     * @param price The price of the Order
     * @param type The type of the Order
     * @param state The state of the Order
     * @param orderDate The date of the Order
     * @param assignationDate The assignation date ID of the Order
     * @param proposalDelivery The proposal delivery of the Order
     * @param deliveryDate The delivery date of the Order
     * @param reviewEmployee The review of the Order
     * @param wineName The wine name of the Order
     * @param employeeUsername The employee username of the Order
     * @param customerUsername The customer username of the Order
     */
    public Order(int idOrder, int quantity, Double price, String type, String state, Date orderDate, Date assignationDate, int proposalDelivery, Date deliveryDate, int reviewEmployee, String wineName, String employeeUsername, String customerUsername) {
        this.idOrder = idOrder;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.state = state;
        this.orderDate = orderDate;
        this.assignationDate = assignationDate;
        this.proposalDelivery = proposalDelivery;
        this.deliveryDate = deliveryDate;
        this.reviewEmployee = reviewEmployee;
        this.wineName = wineName;
        this.employeeUsername = employeeUsername;
        this.customerUsername = customerUsername;
    }

    /**
     * Constructor to instantiate a Order
     * @param type The type of the Order
     * @param state The state of the Order
     * @param quantity The quantity of the Orders
     */
    public Order(String type, String state, int quantity) {
        this.type = type;
        this.state = state;
        this.quantity = quantity;
    }

    /**
     * Getter method of idOrder
     * @return The order ID of the Order
     */
    public int getIdOrder() { return idOrder; }

    /**
     * Getter method of quantity
     * @return The quantity of the Order
     */
    public int getQuantity() { return quantity; }

    /**
     * Getter method of price
     * @return The price of the Order
     */
    public Double getPrice() { return price; }

    /**
     * Getter method of type
     * @return The type of the Order
     */
    public String getType() { return type; }

    /**
     * Getter method of state
     * @return The state of the Order
     */
    public String getState() { return state; }

    /**
     * Getter method of orderDate
     * @return The date of the Order
     */
    public Date getOrderDate() { return orderDate; }

    /**
     * Getter method of assignationDate
     * @return The assegnation date of the Order
     */
    public Date getAssignationDate() { return assignationDate; }

    /**
     * Getter method of proposalDelivery
     * @return The proposal delivery of the Order
     */
    public int getProposalDelivery() { return proposalDelivery; }

    /**
     * Getter method of deliveryDate
     * @return The delivery date of the Order
     */
    public Date getDeliveryDate() { return deliveryDate; }

    /**
     * Getter method of reviewEmployee
     * @return The review of the Order
     */
    public int getReviewEmployee() { return reviewEmployee; }

    /**
     * Getter method of wineName
     * @return The wine name of the Order
     */
    public String getWineName() { return wineName; }

    /**
     * Getter method of employeeUsername
     * @return The employee username of the Order
     */
    public String getEmployeeUsername() { return employeeUsername; }

    /**
     * Getter method of customerUsername
     * @return The customer username of the Order
     */
    public String getCustomerUsername() { return customerUsername; }

    /**
     * Override of toString method
     * @return The string of main variables of the object
     */
    @Override
    public String toString()
    {
        return "Order [id_order=" + idOrder + ", wineName=" + wineName + ", quantity=" + quantity + ", price=" + price + ", type=" + type + ", state=" + state + "]";
    }
}
