package Models;

/**
 * This class contains the getter and setters for the contact model.
 */
public class Contact {
    private int contactId;
    private String contactName;
    private String contactEmail;

    public Contact(int contactId, String contactName, String contactEmail) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /**
     * Get contact ID.
     * @return contactID
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Set contact ID.
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Get contact email.
     * @return contactEmail
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Set contact email.
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * Get contact name.
     * @return contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Set contact name.
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
