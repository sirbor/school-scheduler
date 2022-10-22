package Models;

/**
 * This class contains the getters and setters for the Country model.
 */
public class Country {
    private int countryId;
    private String countryName;

    public Country(int countryId, String countryName) {
        this.countryId = countryId;
        this.countryName = countryName;
    }

    /**
     * Get country ID.
     * @return countryID
     */
    public int getCountryId() {
        return countryId;
    }

    /**
     * Set country ID.
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     * Get country name.
     * @return countryName
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Set country name.
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
