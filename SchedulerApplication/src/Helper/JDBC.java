package Helper;

import Controllers.Helper;
import Models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.Objects;
import java.util.TimeZone;

/**
 * This class contains methods for interacting with the database.
 */
public abstract class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    public static Connection connection;  // Connection Interface
    public static int currentUser;

    public static void openConnection()
    {
        try {
            // Locate driver and authenticate connection.
            Class.forName(driver);
            String password = "Passw0rd!";
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /** Closes the current database connection */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * Get current user
     * @return currentUser
     */
    public static int getCurrentUser() {
        return currentUser;
    }

    /** This method gets the current username
     * @return String of the current username */
    public static String getCurrentUserName(int userId) throws SQLException {
        String userName = null;
        String sql = "SELECT User_Name FROM users WHERE User_ID = " + userId;
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while (rs.next()) {
            userName = rs.getString("User_Name");
        }
        return userName;
    }

    /**
     * Sets the current user
     * @param user ID of the logged in user
     */
    public static void setCurrentUser(int user) {
        currentUser = user;
    }

    /**
     * Returns division name with given ID.
     * @return String divisionName */
    public static String stateNameFromId(int id) throws SQLException {
        String divisionName = null;
        String sql = "SELECT Division FROM first_level_divisions WHERE Division_ID = " + id;
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while (rs.next()) {
            divisionName = rs.getString("Division");
        }
        return divisionName;
    }

    /**
     * Return return division ID from name.
     * @param name String
     * @return int divisionId */
    public static int stateIdFromName(String name) throws SQLException {
        int divisionId = 0;
        String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + name + "'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while (rs.next()) {
            divisionId = rs.getInt("Division_ID");
        }
        return divisionId;
    }

    /**
     * Return country name from division ID.
     * @param id int
     * @return string countryName */
    public static String countryFromDivisionId(int id) throws SQLException {
      int countryId = 0;
      String countryName = null;
      String sql = "SELECT Country_ID FROM first_level_divisions WHERE Division_ID = " + id;
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while (rs.next()) {
            countryId = rs.getInt("Country_ID");
        }
        switch (countryId) {
            case 1 -> countryName = "U.S";
            case 2 -> countryName = "UK";
            case 3 -> countryName = "Canada";
        }
        return countryName;
    }

    /***
     * This method will delete customers from the database.
     * @param customerId */
    public static void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM CUSTOMERS WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);
        ps.executeUpdate();
    }

    /**
     * This method will parse through the user table to find matching username and password combo.
     * @param userName String
     * @param password String
     * @return true if username and password combo matches */
    public static boolean checkLogin(String userName, String password) throws SQLException {
        boolean match = false;
        String sql = "SELECT * FROM USERS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            String user = rs.getString("User_Name");
            String userPassword = rs.getString("Password");
            if((Objects.equals(user, userName)) && (Objects.equals(userPassword, password))) {
                setCurrentUser(rs.getInt("User_ID"));
                match = true;
            }
        }
        return match;
    }

    /**
     * This method gets data from the customer table.
     * @return ObservableList customers */
    public static ObservableList<Customer> getCustomers() throws SQLException {
        String sql = "SELECT * FROM CUSTOMERS";
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            int customerId = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String customerAddress = rs.getString("Address");
            int customerDivision = rs.getInt("Division_ID");
            String customerPostal = rs.getString("Postal_Code");
            String customerPhone = rs.getString("Phone");
            Customer customer = new Customer(customerId, customerName, customerAddress, customerDivision, customerPostal, customerPhone);
            customers.add(customer);
        }
        return customers;
    }

    /**
     * This method gets appointments associated with a customer Id.
     * @return ObservableList divisions */
    public static ObservableList<String> getDivisionsById(String id) throws SQLException {
        String sql = "SELECT Division_ID, Division FROM first_level_divisions WHERE Country_ID=" + id;
        ObservableList<String> divisions = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            String divisionName = rs.getString("Division");
            divisions.add(divisionName);
        }
        return divisions;
    }

    /** This method gets all appointments.
     * @return Observablelist appointments */
    public static ObservableList<Appointment> getAppointments() throws SQLException {
        String sql = "SELECT * FROM APPOINTMENTS";
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        appointments.clear();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int appointmentId = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            int appointmentCustomer = rs.getInt("Customer_ID");
            int appointmentUser = rs.getInt("User_ID");
            int appointmentContact = rs.getInt("Contact_ID");
            Timestamp start = Helper.toLocal(appointmentStart);
            Timestamp end = Helper.toLocal(appointmentEnd);
            Appointment appointment = new Appointment(appointmentId, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, start,
                    end, appointmentCustomer, appointmentUser, appointmentContact);

            appointments.add(appointment);
        }
        return appointments;
    }

    /** This method gets all the users appointments.
     * @return ObservableList appointments */
    public static ObservableList<Appointment> getUserAppointments() throws SQLException {
        String sql = "SELECT * FROM APPOINTMENTS WHERE User_ID = " + getCurrentUser();
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        appointments.clear();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int appointmentId = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            int appointmentCustomer = rs.getInt("Customer_ID");
            int appointmentUser = rs.getInt("User_ID");
            int appointmentContact = rs.getInt("Contact_ID");
            Timestamp start = Helper.toLocal(appointmentStart);
            Timestamp end = Helper.toLocal(appointmentEnd);
            Appointment appointment = new Appointment(appointmentId, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, start,
                    end, appointmentCustomer, appointmentUser, appointmentContact);

            appointments.add(appointment);
        }
        return appointments;
    }

    /** This method gets all appointment for the month.
     * @return ObservableList appointments */
    public static ObservableList<Appointment> getMonthAppointments() throws SQLException {
        String sql = "SELECT * FROM APPOINTMENTS WHERE MONTH(Start) = MONTH(CURRENT_DATE())";
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        appointments.clear();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int appointmentId = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            int appointmentCustomer = rs.getInt("Customer_ID");
            int appointmentUser = rs.getInt("User_ID");
            int appointmentContact = rs.getInt("Contact_ID");
            Timestamp start = Helper.toLocal(appointmentStart);
            Timestamp end = Helper.toLocal(appointmentEnd);
            Appointment appointment = new Appointment(appointmentId, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, start,
                    end, appointmentCustomer, appointmentUser, appointmentContact);

            appointments.add(appointment);
        }
        return appointments;
    }

    /** This method gets all appointments for the week.
     * @return Observablelist appointments */
    public static ObservableList<Appointment> getWeekAppointments() throws SQLException {
        String sql = "SELECT * FROM APPOINTMENTS WHERE WEEK(Start) = WEEK(CURRENT_DATE())";
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        appointments.clear();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int appointmentId = rs.getInt("Appointment_ID");
            String appointmentTitle = rs.getString("Title");
            String appointmentDescription = rs.getString("Description");
            String appointmentLocation = rs.getString("Location");
            String appointmentType = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            int appointmentCustomer = rs.getInt("Customer_ID");
            int appointmentUser = rs.getInt("User_ID");
            int appointmentContact = rs.getInt("Contact_ID");
            Timestamp start = Helper.toLocal(appointmentStart);
            Timestamp end = Helper.toLocal(appointmentEnd);
            Appointment appointment = new Appointment(appointmentId, appointmentTitle, appointmentDescription,
                    appointmentLocation, appointmentType, start,
                    end, appointmentCustomer, appointmentUser, appointmentContact);

            appointments.add(appointment);
        }
        return appointments;
    }

    /**
     * This method gets all appointments associated with a customer ID.
     * @return Observablelist appointments */
    public static ObservableList<Appointment> getAppointmentsById(String id) throws SQLException {
        String sql = "SELECT * FROM APPOINTMENTS WHERE Customer_ID=" + id;
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            int appointmentId = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            Timestamp appointmentStart = rs.getTimestamp("Start");
            Timestamp appointmentEnd = rs.getTimestamp("End");
            int customerId = rs.getInt("Customer_ID");
            int userId = rs.getInt("User_ID");
            int contactId = rs.getInt("Contact_ID");
            Timestamp start = Helper.toLocal(appointmentStart);
            Timestamp end = Helper.toLocal(appointmentEnd);

            Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end, customerId, userId, contactId);
            appointments.add(appointment);
        }
        return appointments;
    }

    /**
     * This method gets divisions into a list.
     * @return ObservableList divisions */
    public static ObservableList<Division> getAllDivisions() throws SQLException {
        String sql = "SELECT * FROM first_level_divisions";
        ObservableList<Division> divisions = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            int divisionId = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            Timestamp createDate = rs.getTimestamp("Create_Date");
            String createdBy = rs.getString("Created_By");
            Timestamp lastUpdate = rs.getTimestamp("Last_Update");
            String lastUpdatedBy = rs.getString("Last_Updated_By");
            int countryId = rs.getInt("Country_ID");

            Division division = new Division(divisionId, divisionName, createDate, createdBy, lastUpdate, lastUpdatedBy, countryId);
            divisions.add(division);
        }
        return divisions;
    }

    /**
     * This method deletes appointments from the database.
     * @param appointmentId int */
    public static void deleteAppointment(int appointmentId) throws SQLException {
        String sql = "DELETE FROM APPOINTMENTS WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentId);
        ps.executeUpdate();
    }

    /**
     * This method get all contacts from the database into a list.
     * @return Observablelist contacts */
    public static ObservableList<Contact> getContacts() throws SQLException {
        String sql = "SELECT * FROM CONTACTS";
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            int contactId = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String contactEmail = rs.getString("Email");
            Contact contact = new Contact(contactId, contactName, contactEmail);
            contacts.add(contact);
        }
        return contacts;
    }

    /** This method returns all contact names.
     * @return Observablelist contactNames*/
    public static ObservableList<String> getContactNames() throws SQLException {
        String sql = "SELECT Contact_Name FROM CONTACTS";
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            String contactName = rs.getString("Contact_Name");
            contactNames.add(contactName);
        }
        return contactNames;
    }

    /** This method returns all customer names.
     * @return Observablelist customerNames */
    public static ObservableList<String> getCustomerNames() throws SQLException {
        String sql = "SELECT Customer_Name FROM CUSTOMERS";
        ObservableList<String> customerNames = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            String customerName = rs.getString("Customer_Name");
            customerNames.add(customerName);
        }
        return customerNames;
    }

    /** This method gets an returns usernames into a list.
     * @return Observablelist userNames*/
    public static ObservableList<String> getUserNames() throws SQLException {
        String sql = "SELECT User_Name FROM USERS";
        ObservableList<String> userNames = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        while(rs.next()) {
            String userName = rs.getString("User_Name");
            userNames.add(userName);
        }
        return userNames;
    }

    /** This method returns a list of all users.
     * @return Observablelist users */
    public static ObservableList<User> getAllUsers() throws SQLException {
        String sql = "SELECT User_ID, User_Name from users";
        ObservableList<User> users = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int userId = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            User user = new User(userId, null, userName);
            users.add(user);
        }
        return users;
    }

    /**
     * This method will edit customers on the database.
     * @param customerId int
     * @param customerName String
     * @param address String
     * @param postal String
     * @param phone String
     * @param divisionID int
     * */
    public static void updateCustomer(int customerId, String customerName, String address, String postal,
                                      String phone, int divisionID) throws SQLException {
        String sql = "UPDATE CUSTOMERS SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, "+
                "Last_Update = CURRENT_TIMESTAMP, Last_Updated_by = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, postal);
        ps.setString(4, phone);
        ps.setInt(5, getCurrentUser());
        ps.setInt(6, divisionID);
        ps.setInt(7, customerId);
        ps.executeUpdate();
    }

    /**
     * This method will add customers to the database.
     * @param customerName String
     * @param address String
     * @param postal String
     * */
    public static void addCustomer(String customerName, String address, String postal,
                                   String phone, int divisionID) throws SQLException {
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, "+
                "Create_Date, Created_By, Last_Update, Last_Updated_by, Division_ID)"+
                "VALUES (?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, address);
        ps.setString(3, postal);
        ps.setString(4, phone);
        ps.setString(5, getCurrentUserName(currentUser));
        ps.setInt(6, getCurrentUser());
        ps.setInt(7, divisionID);
        ps.executeUpdate();
    }

    /**
     * This method will add appointments to the database.
     * @param title String appointment title
     * @param description String appt description
     * @param location String appt location
     * @param type String appt type
     * @param start Timestamp appt start time
     * @param end Timestamp appt end time
     * @param customerId int
     * @param userId int
     * @param contactId int
     * @throws SQLException
     */
    public static void addAppointment(String title, String description, String location, String type, Timestamp start, Timestamp end, int customerId, int userId, int contactId) throws SQLException {
        String sql = "INSERT INTO APPOINTMENTS (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?)";
        // Time conversion to UTC
        start = Helper.toUTC(start);
        end = Helper.toUTC(end);
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, start);
        ps.setTimestamp(6, end);
        ps.setString(7, getCurrentUserName(currentUser));
        ps.setString(8, getCurrentUserName(currentUser));
        ps.setInt(9, customerId);
        ps.setInt(10, userId);
        ps.setInt(11, contactId);
        ps.executeUpdate();
    }

    /**
     * This method will update appointments in the database.
     * @param appointmentId int
     * @param title String
     * @param description String
     * @param location String
     * @param type String
     * @param start Timestamp
     * @param end Timestamp
     * @param customerId int
     * @param userId int
     * @param contactId int
     * @throws SQLException
     */
    public static void updateAppointment(int appointmentId, String title, String description, String location, String type, Timestamp start, Timestamp end, int customerId, int userId, int contactId) throws SQLException {
        String sql = "UPDATE APPOINTMENTS SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = CURRENT_TIMESTAMP, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
        // Convert time to UTC
        start = Helper.toUTC(start);
        end = Helper.toUTC(end);
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, start);
        ps.setTimestamp(6, end);
        ps.setString(7, getCurrentUserName(currentUser));
        ps.setInt(8, customerId);
        ps.setInt(9, userId);
        ps.setInt(10, contactId);
        ps.setInt(11, appointmentId);
        ps.executeUpdate();
    }

    /**
     *
     * @param contactId int
     * @return String of contact name
     * @throws SQLException
     */
    public static String getContactName(int contactId) throws SQLException {
        String sql = "SELECT Contact_Name FROM CONTACTS WHERE Contact_ID = " + contactId;
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        String contactName = "";
        while(rs.next()) {
            contactName = rs.getString("Contact_Name");
        }
        return contactName;
    }

    /**
     *
     * @param customerId int
     * @return String customerName
     * @throws SQLException
     */
    public static String getCustomerName(int customerId) throws SQLException {
        String sql = "SELECT Customer_Name FROM CUSTOMERS WHERE Customer_ID = " + customerId;
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        String customerName = "";
        while(rs.next()) {
            customerName = rs.getString("Customer_Name");
        }
        return customerName;
    }

    /**
     *
     * @param userId int
     * @return String of user name
     * @throws SQLException
     */
    public static String getUserName(int userId) throws SQLException {
        String sql = "SELECT User_Name FROM USERS WHERE User_ID = " + userId;
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        String userName = "";
        while(rs.next()) {
            userName = rs.getString("User_Name");
        }
        return userName;
    }

    /**
     *
     * @param userName String
     * @return String user Id
     * @throws SQLException
     */
    public static int getUserId(String userName) throws SQLException {
        String sql = "SELECT User_ID FROM USERS WHERE User_Name = '" + userName + "'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        int userId = 0;
        while(rs.next()) {
            userId = rs.getInt("User_ID");
        }
        return userId;
    }

    /**
     *
     * @param contactName String
     * @return int contact Id
     * @throws SQLException
     */
    public static int getContactId(String contactName) throws SQLException {
        String sql = "SELECT Contact_ID FROM CONTACTS WHERE Contact_Name = '" + contactName + "'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        int contactId = 0;
        while(rs.next()) {
            contactId = rs.getInt("Contact_ID");
        }
        return contactId;
    }

    /** This method will generate a report for all appointment contacts.
     * @param id Contact ID
     * @return ObservableList */
    public static ObservableList<String> userAppointmentByID(String id) throws SQLException {
        ObservableList<String> appointments = FXCollections.observableArrayList();
        String sql = "SELECT * FROM APPOINTMENTS WHERE User_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, id);
        ResultSet results = ps.executeQuery();

        while (results.next()) {
            String appointmentId = results.getString("Appointment_ID");
            String title = results.getString("Title");
            String type = results.getString("Type");
            String description = results.getString("Description");
            Timestamp start = results.getTimestamp("Start");
            Timestamp end = results.getTimestamp("End");
            String customerId = results.getString("Customer_ID");
            Timestamp localStart = Helper.toLocal(start);
            Timestamp localEnd = Helper.toLocal(end);

            String line = "Appointment ID: " + appointmentId + "\n";
            line += "Title: " + title + "\n";
            line += "Type: " + type + "\n";
            line += "Description: " + description + "\n";
            line += "Start date/time: " + localStart + " " + TimeZone.getDefault().getDisplayName() + "\n";
            line += "End date/time: " + localEnd + " " + TimeZone.getDefault().getDisplayName() + "\n";
            line += "Customer ID: " + customerId + "\n\n";
            appointments.add(line);
        }
        return appointments;
    }

    /** This method will generate a report for all appointment contacts.
     * @param id Contact ID
     * @return ObservableList */
    public static ObservableList<String> contactAppointmentsById(String id) throws SQLException {
        ObservableList<String> appointments = FXCollections.observableArrayList();
        String sql = "SELECT * FROM APPOINTMENTS WHERE Contact_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, id);
        ResultSet results = ps.executeQuery();

        while (results.next()) {
            String appointmentId = results.getString("Appointment_ID");
            String title = results.getString("Title");
            String type = results.getString("Type");
            String description = results.getString("Description");
            Timestamp start = results.getTimestamp("Start");
            Timestamp end = results.getTimestamp("End");
            String customerId = results.getString("Customer_ID");
            Timestamp localStart = Helper.toLocal(start);
            Timestamp localEnd = Helper.toLocal(end);

            String line = "Appointment ID: " + appointmentId + "\n";
            line += "Title: " + title + "\n";
            line += "Type: " + type + "\n";
            line += "Description: " + description + "\n";
            line += "Start date/time: " + localStart + " " + TimeZone.getDefault().getDisplayName() + "\n";
            line += "End date/time: " + localEnd + " " + TimeZone.getDefault().getDisplayName() + "\n";
            line += "Customer ID: " + customerId + "\n\n";
            appointments.add(line);
        }
        return appointments;
    }

    /**
     *
     * @param customerName String
     * @return int customer Id
     * @throws SQLException
     */
    public static int getCustomerId(String customerName) throws SQLException {
        String sql = "SELECT Customer_ID FROM CUSTOMERS WHERE Customer_Name = '" + customerName + "'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        int customerId = 0;
        while(rs.next()) {
            customerId = rs.getInt("Customer_ID");
        }
        return customerId;
    }

}
