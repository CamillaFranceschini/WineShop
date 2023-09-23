package wineshop.model;

import java.io.Serializable;

/**
 * Class for represent information about an Employee
 */
public class Employee extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID of the Employee
     */
    private int id;
    /**
     * Name of the Employee
     */
    private String name;
    /**
     * Surname of the Employee
     */
    private String surname;
    /**
     * NIN of the Employee
     */
    private String cf;
    /**
     * Email of the Employee
     */
    private String email;
    /**
     * Phone number of the Employee
     */
    private String phone;
    /**
     * Address of the Employee
     */
    private String address;
    /**
     * Username of the Employee
     */
    private String username;
    /**
     * Password of the Employee
     */
    private String password;
    /**
     * True if is an admin, false otherwise
     */
    private boolean admin;
    /**
     * Average reviews of the Employee
     */
    private Double avgReview;

    /**
     * Constructor to instantiate an Employee
     * @param name The name of the Employee
     * @param surname The surname of the Employee
     * @param cf The NIN of the Employee
     * @param email The email of the Employee
     * @param phone The phone number of the Employee
     * @param address The address of the Employee
     * @param username The username of the Employee
     * @param password The password of the Employee
     * @param admin True if is an admin, false otherwise
     */
    public Employee(String name, String surname, String cf, String email, String phone, String address, String username, String password, boolean admin)
    {
        this.name = name;
        this.surname = surname;
        this.cf = cf;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.username = username;
        this.password= password;
        this.admin = admin;
    }

    /**
     * Constructor to instantiate an Employee
     * @param username The username of the Employee
     * @param avgReview The average reviews of the Employee
     */
    public Employee(String username, Double avgReview)
    {
        this.username = username;
        this.avgReview = avgReview;
    }

    /**
     * Constructor to instantiate an Employee
     * @param id The ID of the Employee
     * @param username The username of the Employee
     */
    public Employee(int id, String username)
    {
        this.id = id;
        this.username = username;
    }

    /**
     * Getter method of id
     * @return The ID of the Employee
     */
    public int getId() { return id; }

    /**
     * Getter method of name
     * @return The name of the Employee
     */
    public String getName() { return name; }

    /**
     * Getter method of surname
     * @return The surname of the Employee
     */
    public String getSurname() { return surname; }

    /**
     * Getter method of cf
     * @return The NIN of the Employee
     */
    public String getCf() { return cf; }

    /**
     * Getter method of email
     * @return The email of the Employee
     */
    public String getEmail() { return email; }

    /**
     * Getter method of phone
     * @return The phone number of the Employee
     */
    public String getPhone() { return phone; }

    /**
     * Getter method of address
     * @return The address of the Employee
     */
    public String getAddress() { return address; }

    /**
     * Getter method of username
     * @return The username of the Employee
     */
    public String getUsername() { return username; }

    /**
     * Getter method of password
     * @return The password of the Employee
     */
    public String getPassword() { return password; }

    /**
     * Getter method of admin
     * @return True if is an admin, false otherwise
     */
    public boolean getAdmin() { return admin; }

    /**
     * Getter method of avgReview
     * @return The average reviews of the Employee
     */
    public Double getAvgReview() { return avgReview; }

    /**
     * Override of toString method
     * @return The string of main variables of the object
     */
    @Override
    public String toString()
    {
        return "Employee [name=" + name + ", surname=" + surname + ", CF=" + cf + ", email=" + email + ", phone=" + phone + ", address=" + address + ", admin=" + admin + "]";
    }
}
