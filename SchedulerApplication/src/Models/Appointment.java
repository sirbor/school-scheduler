package Models;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This class contains all the getters and setters for the appointment model.
 */
public class Appointment {
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private Timestamp start;
    private Timestamp end;
    private int customer_id;
    private int user_id;
    private int contact_id;

    public Appointment(int id, String title, String description, String location, String type, Timestamp start,
                       Timestamp end, int customer_id, int user_id, int contact_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customer_id = customer_id;
        this.user_id = user_id;
        this.contact_id = contact_id;
    }

    /**
     * Get appointment id.
     * @return appointment id
     */
    public int getId() {
        return id;
    }

    /**
     * Set appointment id.
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get appointment title.
     * @return appt title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set appointment title.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get start time of appointment.
     * @return start time
     */
    public LocalTime getStartTime() {
        return start.toLocalDateTime().toLocalTime();
    }

    /**
     * Get appointment start date.
     * @return start date
     */
    public LocalDate getStartDate() {
        return start.toLocalDateTime().toLocalDate();
    }

    /**
     * Get appointment location.
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set appointment location.
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get appointment description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set appointment description.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get appointment type.
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Set appointment type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Time stamp to local date time conversion.
     * @return localdatetime
     */
    public LocalDateTime getStartLocalDateTime() {
        return start.toLocalDateTime();
    }

    /**
     * Get start time of appointment.
     * @return start time
     */
    public Timestamp getStart() {
        return start;
    }

    /**
     * Get end time of appointment.
     * @return end time
     */
    public Timestamp getEnd() {
        return end;
    }

    /**
     * Get customer ID for appointment.
     * @return customer Id
     */
    public int getCustomer_id() {
        return customer_id;
    }

    /**
     * Set customer ID for appointment.
     */
    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    /**
     * Get user Id for appointment.
     * @return user id
     */
    public int getUser_id() {
        return user_id;
    }

    /**
     * Set user Id for appointment.
     */
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * Get contact Id for appointment.
     * @return contact id
     */
    public int getContact_id() {
        return contact_id;
    }

    /**
     * Set contact Id for appointment.
     */
    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }
}
