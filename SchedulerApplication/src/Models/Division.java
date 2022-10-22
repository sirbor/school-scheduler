package Models;

import java.sql.Timestamp;

/**
 * This class has all the getters and setters for the division model.
 */
public class Division {
    private int divisionId;
    private String divisionName;
    private int CountryId;

    public Division(int divisionId, String divisionName, Timestamp createDate,
                    String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int countryId) {
        this.divisionId = divisionId;
        this.divisionName = divisionName;
        CountryId = countryId;
    }

    // Class constructor
    public Division(String divisionName) {
    }

    /**
     * Get divisionID.
     * @return divisionID
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Set divisionID.
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * Get divisionName.
     * @return divisionName
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * Set divisionName.
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * Get countryId.
     * @return countryId
     */
    public int getCountryId() {
        return CountryId;
    }

    /**
     * Set countryId.
     */
    public void setCountryId(int countryId) {
        CountryId = countryId;
    }
}
