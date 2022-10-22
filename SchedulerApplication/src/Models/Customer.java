package Models;

/**
 * This class contains the getters and setters for the Customer model.
 */
public class Customer {
    private int id;
    private String name;
    private String address;
    private int division;
    private String postalCode;
    private String phoneNumber;

    public Customer(int id, String name, String address, int division, String postalCode, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.division = division;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get ID.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get address.
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get division.
     * @return division
     */
    public int getDivision() {
        return division;
    }

    /**
     * Set division.
     */
    public void setDivision(int division) {
        this.division = division;
    }

    /**
     * Get postalCode.
     * @return postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Set postalCode.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Get phoneNumber.
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set phoneNumber.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
