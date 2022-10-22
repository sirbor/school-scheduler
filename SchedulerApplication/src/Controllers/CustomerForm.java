package Controllers;

import Models.Appointment;
import Models.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static Helper.JDBC.*;

/**
 * This contains all the methods that control the behaviour of the customers form.
 * Adding and editing customers.
 */
public class CustomerForm extends Helper implements Initializable {
    @FXML private TableView<Customer> customersTableview;
    @FXML private TableColumn<Customer, Integer> customerIdColumn;
    @FXML private TableColumn<Customer, String> customerNameColumn;
    @FXML private TableColumn<Customer, String> customerAddressColumn;
    @FXML private TableColumn<Customer, Integer> customerDivisionColumn;
    @FXML private TableColumn<Customer, String> customerPostalColumn;
    @FXML private TableColumn<Customer, String> customerPhoneColumn;
    @FXML private Button editCustomerButton;
    @FXML private Button addCustomerButton;
    @FXML private Button deleteCustomerButton;
    @FXML private TableView<Appointment> appointmentsTableview;
    @FXML private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML private TableColumn<Appointment, String> appointmentDescriptionColumn;
    @FXML private TableColumn<Appointment, String> appointmentLocationColumn;
    @FXML private TableColumn<Appointment, String> appointmentTypeColumn;
    @FXML private TableColumn<Appointment, Timestamp> appointmentStartColumn;
    @FXML private TableColumn<Appointment, Timestamp> appointmentEndColumn;
    @FXML private TableColumn<Appointment, Integer> appointmentContactColumn;
    @FXML private TableColumn<Appointment, Integer> appointmentCustomerColumn;
    @FXML private TableColumn<Appointment, Integer> appointmentUserColumn;
    @FXML private DatePicker dateFilter;
    @FXML private RadioButton monthRadioButton;
    @FXML private RadioButton weekRadioButton;
    public static Customer selectedCustomer = null;
    public static Appointment selectedAppointment = null;
    public static boolean addingCustomer;
    public static boolean addingAppointment;
    @FXML private ChoiceBox<String> reportChoice;

    /**
     * This will get the selected appointment.
     * @return selected appointment
     */
    public static Appointment getSelectedAppointment() { return selectedAppointment; }

    /**
     * This will get the selected customer.
     * @return selected Customer
     */
    public static Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    /**
     * This will check if customer has appointments and if they do, then customer cannot
     * be deleted, otherwise customer can be deleted.
     * @param customerId
     * @return boolean false if customer has appointments.
     * @throws SQLException
     */
    public boolean checkForAppointments(int customerId) throws SQLException {
        return getAppointmentsById(String.valueOf(customerId)).size() != 0;
    }

    /**
     * This will open the edit customer form.
     * @param event
     * @throws IOException
     */
    public void handleEditCustomer(ActionEvent event) throws IOException {
        try {
            // Error if there is no customer selected.
            if (customersTableview.getSelectionModel().getSelectedItem() == null) {
                Helper.errorDialog("Please select a customer to edit.");
            } else {
            // Gets the information of the selected customer.
                selectedCustomer = customersTableview.getSelectionModel().getSelectedItem();
                Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/editCustomerForm.fxml")));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e){
            e.printStackTrace();
            Helper.errorDialog("The customer was NOT edited.");
        }
    }

    /**
     * This will open the add customer form.
     * @param event
     * @throws IOException
     */
    public void handleAddCustomer(ActionEvent event) throws IOException {
        // Shows customer is being added.
        addingCustomer = true;
        // Opens the edit customer dialog.
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/editCustomerForm.fxml")));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method will delete customers from the database only if customers don't have an appointment.
     * @throws SQLException
     */
    public void handleDeleteCustomer() throws SQLException {

        selectedCustomer = customersTableview.getSelectionModel().getSelectedItem();
        // Error if customer is not selected.
        if (customersTableview.getSelectionModel().getSelectedItem() == null) {
            Helper.errorDialog("Select a customer to be deleted.");
        } else {
            if (checkForAppointments(selectedCustomer.getId())) {
                Helper.errorDialog("Customer is still associated with 1 or more appointments. Please delete and associated appoints to delete customer.");
            } else {
                // Get the information of the selected customer.
                Customer selectedCustomer = customersTableview.getSelectionModel().getSelectedItem();
                // Delete the customer from the database.
                try {
                    deleteCustomer(selectedCustomer.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                    Helper.errorDialog("Problem deleting customer. Please try again.");
                }
                // Refresh the table view
                Helper.errorDialog("Customer ID: " + selectedCustomer.getId() + " with the name " + selectedCustomer.getName() + " successfully deleted.");
                customersTableview.getItems().remove(selectedCustomer);
            }
        }
    }

    /**
     * This method will edit the selected appointment.
     * @param event
     * @throws IOException
     */
    public void handleEditAppointment(ActionEvent event) throws IOException {
        try {
            // Error if no appointment is selected.
            if (appointmentsTableview.getSelectionModel().getSelectedItem() == null) {
                Helper.errorDialog("Please select an appointment to edit.");
            } else {
                // Get the information of the selected appointment.
                selectedAppointment = appointmentsTableview.getSelectionModel().getSelectedItem();
                Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/AppointmentForm.fxml")));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e){
            e.printStackTrace();
            Helper.errorDialog("Appointment was NOT edited.");
        }
    }

    /**
     * This will add appointment and open the appointment form.
     * @param event
     * @throws IOException
     */
    public void handleAddAppointment(ActionEvent event) throws IOException {
        addingAppointment = true;
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/AppointmentForm.fxml")));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method will delete the selected appointment.
     */
    public void handleDeleteAppointment() {
        // Gets the information of the selected appointment.
        selectedAppointment = appointmentsTableview.getSelectionModel().getSelectedItem();
        // Error if no appointment is selected.
        if (appointmentsTableview.getSelectionModel().getSelectedItem() == null) {
            Helper.errorDialog("Please select an appointment to delete.");
        } else {
            // This will delete the appointment from the database.
            try {
                deleteAppointment(selectedAppointment.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                Helper.errorDialog("Appointment NOT deleted");
            }
            // Refresh the table.
            // Dialog will show deleted appointment ID and appointment type.
            Helper.noticeDialog("Appointment ID: " + selectedAppointment.getId() + " \nType: " + selectedAppointment.getType() + " \ndeleted.");
            appointmentsTableview.getItems().remove(selectedAppointment);
        }
    }

    /**
     * This method will get the users appointments.
     * @throws SQLException
     */
    public void handleMyAppointments() throws SQLException {
        weekRadioButton.setSelected(false);
        monthRadioButton.setSelected(false);
        customersTableview.getSelectionModel().clearSelection();
        ObservableList<Appointment> myAppointments = getUserAppointments();
        populateAppointments(myAppointments);
    }

    /**
     * This method will notify the user if they have any appointments within 15 minutes.
     * @throws SQLException
     */
    private void checkForUpcomingAppointments() throws SQLException {
        boolean hasAppointments = false;
        for (Appointment appointment : getUserAppointments()) {
            if (Duration.between(LocalDateTime.now(), appointment.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()).toMinutes() <= 15 &&
                    Duration.between(LocalDateTime.now(), appointment.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()).toMinutes() >= 0) {
                Helper.errorDialog("You have an upcoming appointment:\n" + "Appointment: " + appointment.getId() + "\nStarts at: " + appointment.getStart());
                hasAppointments = true;
                break;
            }
        }
        if (!hasAppointments) {
            Helper.errorDialog("No upcoming appointments.");
        }
    }

    /**
     * LAMBDA EXPRESSION: Converts start time stamp to a local date time and checks if the appointment is in the selected month.
     * Get appointments in the current month.
     * @throws SQLException
     */
    public void handleMonthRadio() throws SQLException {
        Month selectedMonth = dateFilter.getValue().getMonth();
        weekRadioButton.setSelected(false);
        if (selectedMonth != null) {
            ObservableList<Appointment> appointmentsThisMonth = getAppointments().filtered(a -> a.getStart().toLocalDateTime()
                    .getMonth() == selectedMonth);
            populateAppointments(appointmentsThisMonth);
        }
    }

    /**
     * LAMBDA EXPRESSION: Converts start time stamp to a local date time and checks if the appointment is in the selected month.
     * Get appointments in the current month.
     * Populated the appointment table with appointments occurring on the selected week.
     * @throws SQLException
     */
    public void handleWeekRadio() throws SQLException {
        WeekFields weekFields = WeekFields.of(Locale.US);
        int selectedWeek = dateFilter.getValue().get(weekFields.weekOfWeekBasedYear());
        monthRadioButton.setSelected(false);
        if (selectedWeek != 0) {
            ObservableList<Appointment> appointmentsThisWeek = getAppointments().filtered(
                    appointment -> appointment.getStart().toLocalDateTime()
                            .get(weekFields.weekOfWeekBasedYear()) == selectedWeek);
            populateAppointments(appointmentsThisWeek);
        }
    }

    /**
     * This will show a report based on the user selections.
     * @throws SQLException
     */
    public void handleReportButton() throws SQLException {
        String reportType = reportChoice.getValue();
        switch (reportType) {
            case "Customer Appointments":
                Helper.reportDialog("Customer Appointments", "Number of Customer Appointments.", reportTotalsByTypeAndMonth());
                break;
            case "Contact Schedules":
                Helper.reportDialog("Contact Schedules", "Schedule for every client", createContactSchedule());
                break;
            case "User Schedules":
                Helper.reportDialog("User Schedules", "Schedule for every user.", createUserSchedule());
                break;
            default:
                Helper.errorDialog("Choose the report to view.");
        }
    }

    /** This method will generate a report with the total number of customer appointments by type and month.
     * @return String */
    public static String reportTotalsByTypeAndMonth() throws SQLException {
        String report = "";
        String typeStrings = "";
        String monthStrings = "";
        report += "Customer appointments by type and month:\n";
        String type = "SELECT Type, COUNT(Type) as \"Total\" FROM appointments GROUP BY Type";
        PreparedStatement getTypes = connection.prepareStatement(type);
        String month = "SELECT MONTHNAME(Start) as \"Month\", COUNT(MONTH(Start)) as \"Total\" from appointments GROUP BY Month";
        PreparedStatement getMonths = connection.prepareStatement(month);

        ResultSet typeResults = getTypes.executeQuery();
        ResultSet monthResults = getMonths.executeQuery();

        while (typeResults.next()) {
            typeStrings = "Type: " + typeResults.getString("Type") + " Count: " +
                    typeResults.getString("Total") + "\n";
            report += typeStrings;
        }

        while (monthResults.next()) {
            monthStrings = "Month: " + monthResults.getString("Month") + " Count: " +
                    monthResults.getString("Total") + "\n";
            report += monthStrings;

        }
        getMonths.close();
        getTypes.close();
        return report;
    }

    /** This method will generate a schedule for every contact.
     * @return String */
    public String createContactSchedule() throws SQLException {
        String report = "";
        ObservableList<String> contacts = getContactNames();

        for (String contact : contacts) {
            String contactID = String.valueOf(getContactId(contact));
            report += "Contact Name: " + contact + " (ID: " + contactID + ")\n\n";
            report += "---------------\n";

            ObservableList<String> appointments = contactAppointmentsById(contactID);
            if(appointments.isEmpty()) {
                report += "  No appointments for contact \n\n";
            }
            for (String appointment : appointments) {
                report += appointment;
            }
        }
        return report;
    }

    /**
     * This will show a report of the schedule for every user.
     * @return report String
     * @throws SQLException
     */
    public String createUserSchedule() throws SQLException {
        ObservableList<String> users = getUserNames();
        String report = "Schedule for users: \n";
        for (String user: users) {
            String userID = String.valueOf(getUserId(user));
            report += "\nUser Name: " + user + " (ID: " + userID + ")\n\n";
            report += "---------------\n";

            ObservableList<String> appointments = userAppointmentByID(userID);
            if(appointments.isEmpty()) {
                report += "  No appointments for contact \n\n";
            }
            for (String appointment : appointments) {
                report += appointment;
            }
        }
    return report;
    }

    /**
     * Gets and Sets appointment table.
     * @param appointmentList listed appointments.
     */
    public void populateAppointments(ObservableList<Appointment> appointmentList) {
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact_id"));
        appointmentCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        appointmentUserColumn.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        appointmentsTableview.setItems(appointmentList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // When user logs in, checks if they have appointments.
        if (LoginForm.initialLogon) {
            try {
                checkForUpcomingAppointments();
                LoginForm.initialLogon = false;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Set date to today's date.
        dateFilter.setValue(LocalDate.now());
        addingAppointment = false;
        addingCustomer = false;
        ObservableList<String> reports = FXCollections.observableArrayList("Customer Appointments", "Contact Schedules", "User Schedules");
        reportChoice.setItems(reports);
        // Populate customer table.
        try {
            customersTableview.getItems().setAll(getCustomers());
            customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            customerDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("division"));
            customerPostalColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            customersTableview.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            customersTableview.setItems(getCustomers());
            

            //** LAMBDA EXPRESSION: This will populate the appointment table with the detected customer information.
            customersTableview.setRowFactory(tv -> {
                TableRow<Customer> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    // This will deselect radio buttons if they are selected.
                    weekRadioButton.setSelected(false);
                    monthRadioButton.setSelected(false);
                    // Check for empty rows.
                    if (!row.isEmpty()) {
                        Customer element = row.getItem();
                        int col = element.getId();
                        try {
                            appointmentsTableview.getItems().setAll(getAppointmentsById(String.valueOf(col)));
                            appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                            appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
                            appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
                            appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
                            appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
                            appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
                            appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
                            appointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact_id"));
                            appointmentCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
                            appointmentUserColumn.setCellValueFactory(new PropertyValueFactory<>("user_id"));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return row;
            });

        } catch (SQLException e) {
            Helper.errorDialog("Could NOT retrieve customers.");
        }
    }
}
