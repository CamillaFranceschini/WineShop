package wineshop.model;

import java.io.Serializable;

/**
 * Class for represent information about a Customer
 */
public class Customer extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID of the Customer
     */
    private int id;

    /**
     * Name of the Customer
     */
    private String name;
    /**
     * Surname of the Customer
     */
    private String surname;
    /**
     * NIN of the Customer
     */
    private String cf;
    /**
     * Email of the Customer
     */
    private String email;
    /**
     * Phone number of the Customer
     */
    private String phone;
    /**
     * Address of the Customer
     */
    private String address;
    /**
     * Username of the Customer
     */
    private String username;
    /**
     * Password of the Customer
     */
    private String password;

    /**
     * Constructor to instantiate a Customer
     * @param name The name of the Customer
     * @param surname The surname of the Customer
     * @param cf The NIN of the Customer
     * @param email The email of the Customer
     * @param phone The phone number of the Customer
     * @param address The address of the Customer
     * @param username The username of the Customer
     * @param password The password of the Customer
     */
    public Customer(String name, String surname, String cf, String email, String phone, String address, String username, String password)
    {
        this.name = name;
        this.surname = surname;
        this.cf = cf;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.username = username;
        this.password= password;
    }

    /**
     * Constructor to instantiate a Customer
     * @param id The ID of the Customer
     * @param username The username of the Customer
     */
    public Customer(int id, String username)
    {
        this.id = id;
        this.username = username;
    }

    /**
     * Getter method of id
     * @return The ID of the Customer
     */
    public int getId() { return id; }

    /**
     * Getter method of name
     * @return The name of the Customer
     */
    public String getName() { return name; }

    /**
     * Getter method of surname
     * @return The name of the Customer
     */
    public String getSurname() { return surname; }

    /**
     * Getter method of NIN
     * @return The NIN of the Customer
     */
    public String getCf() { return cf; }

    /**
     * Getter method of email
     * @return The email of the Customer
     */
    public String getEmail() { return email; }

    /**
     * Getter method of phone
     * @return The phone number of the Customer
     */
    public String getPhone() { return phone; }

    /**
     * Getter method of address
     * @return The address of the Customer
     */
    public String getAddress() { return address; }

    /**
     * Getter method of username
     * @return The username of the Customer
     */
    public String getUsername() { return username; }

    /**
     * Getter method of password
     * @return The password of the Customer
     */
    public String getPassword() { return password; }

    /**
     * Override of toString method
     * @return The string of main variables of the object
     */
    @Override
    public String toString()
    {
        return "Customer [name=" + name + ", surname=" + surname + ", NIN=" + cf + ", email=" + email + ", phone=" + phone + ", address=" + address + "]";
    }
}
