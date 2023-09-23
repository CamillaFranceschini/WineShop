package wineshop.model;

import java.io.Serializable;

/**
 * Class for represent information about a Wine
 */
public class Wine implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * ID of the Wine
     */
    private int id;
    /**
     * Name of the Wine
     */
    private String name;
    /**
     * Producer of the Wine
     */
    private String producer;
    /**
     * Origin of the Wine
     */
    private String origin;
    /**
     * Year of the Wine
     */
    private int year;
    /**
     * Technical notes of the Wine
     */
    private String technicalNotes;
    /**
     * Vines of the Wine
     */
    private String vines;
    /**
     * Sale price of the Wine
     */
    private Double salePrice;
    /**
     * Purchase price of the Wine
     */
    private Double purchasePrice;
    /**
     * Threshold of the Wine
     */
    private int threshold;
    /**
     * Courier name of the Wine
     */
    private String courierName;
    /**
     * Supplier name of the Wine
     */
    private String supplierName;
    /**
     * Quantity of the Wine
     */
    private int quantity;

    /**
     * Constructor to instantiate a Wine
     * @param id The ID of the Wine
     * @param name The name of the Wine
     * @param producer The producer of the Wine
     * @param origin The origin of the Wine
     * @param year The year of the Wine
     * @param technicalNotes The technical notes of the Wine
     * @param vines The vines of the Wine
     * @param salePrice The sale price of the Wine
     * @param purchasePrice The purchase price of the Wine
     * @param threshold The threshold of the Wine
     * @param courierName The courier name of the Wine
     * @param supplierName The supplier name of the Wine
     */
    public Wine(int id, String name, String producer, String origin, int year, String technicalNotes, String vines, Double salePrice, Double purchasePrice, int threshold, String courierName, String supplierName) {
        this.id = id;
        this.name = name;
        this.producer = producer;
        this.origin = origin;
        this.year = year;
        this.technicalNotes = technicalNotes;
        this.vines = vines;
        this.salePrice = salePrice;
        this.purchasePrice = purchasePrice;
        this.threshold = threshold;
        this.courierName = courierName;
        this.supplierName = supplierName;
    }

    /**
     * Constructor to instantiate a Wine
     * @param name The name of the Wine
     * @param quantity The quantity of the Wine
     */
    public Wine(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Getter method of id
     * @return The ID of the Wine
     */
    public int getId() { return id; }

    /**
     * Getter method of name
     * @return The name of the Wine
     */
    public String getName() { return name; }

    /**
     * Getter method of producer
     * @return The producer of the Wine
     */
    public String getProducer() { return producer; }

    /**
     * Getter method of origin
     * @return The origin of the Wine
     */
    public String getOrigin() { return origin; }

    /**
     * Getter method of year
     * @return The year of the Wine
     */
    public int getYear() { return year; }

    /**
     * Getter method of technicalNotes
     * @return The technical notes of the Wine
     */
    public String getTechnicalNotes() { return technicalNotes; }

    /**
     * Getter method of vines
     * @return The vines of the Wine
     */
    public String getVines() { return vines; }

    /**
     * Getter method of salePrice
     * @return The sale price of the Wine
     */
    public Double getSalePrice() { return salePrice; }

    /**
     * Getter method of purchasePrice
     * @return The purchase price of the Wine
     */
    public Double getPurchasePrice() { return purchasePrice; }

    /**
     * Getter method of threshold
     * @return The threshold of the Wine
     */
    public int getThreshold() { return threshold; }

    /**
     * Getter method of courierName
     * @return The courier name of the Wine
     */
    public String getCourierName() { return courierName; }

    /**
     * Getter method of supplierName
     * @return The supplier name of the Wine
     */
    public String getSupplierName() { return supplierName; }

    /**
     * Getter method of quantity
     * @return The quantity of the Wine
     */
    public int getQuantity() { return quantity; }

    /**
     * Override of toString method
     * @return The string of main variables of the object
     */
    @Override
    public String toString()
    {
        return "Wine [name=" + name + ", producer=" + producer + ", origin=" + origin + ", year=" + year + ", technicalNotes=" + technicalNotes + ", vines=" + vines + "]";
    }
}
