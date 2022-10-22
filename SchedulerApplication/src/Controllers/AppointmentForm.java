package Controllers;

import Helper.JDBC;
import Models.Appointment;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class contains the methods that handle the behavior of the appointment form.
 * Add and edit appointments.
 */
public class AppointmentForm implements Initializable {

    @FXML private TextField appointmentDescriptionTextField;
    @FXML private TextField appointmentIdTextField;
    @FXML private TextField appointmentLocationTextField;
    @FXML private TextField appointmentTitleTextField;
    @FXML private TextField appointmentTypeTextField;
    @FXML private Button cancelButton;
    @FXML private ComboBox<String> contactCombo;
    @FXML private ComboBox<String> customerCombo;
    @FXML private AnchorPane endDate;
    @FXML private DatePicker endDatePicker;
    @FXML private ChoiceBox<String> endHourChoice;
    @FXML private ChoiceBox<String> endMinuteChoice;
    @FXML private Button saveButton;
    @FXML private DatePicker startDatePicker;
    @FXML private ChoiceBox<String> startHourChoice;
    @FXML private ChoiceBox<String> startMinuteChoice;
    @FXML private ComboBox<String> userCombo;
    private final ObservableList<String> hours = FXCollections.observableArrayList();
    private final ObservableList<String> minutes = FXCollections.observableArrayList();
    private final Appointment selectedAppointment = CustomerForm.getSelectedAppointment();

    /**
     * This method handles the behavior of the cancel button.
     * @param event
     * @throws IOException
     */
    public void handleCancelButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/CustomerForm.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method determines the behavior of the save button.
     * @param event
     * @throws SQLException
     * @throws IOException
     */
    public void handleSaveButton(ActionEvent event) throws SQLException, IOException {
        if (!checkInputs()) {
            Helper.errorDialog("Please fill out all required fields.");
        } else {
            // This converts startTime to Timestamp
            Timestamp startTime = Timestamp.valueOf(startDatePicker.getValue().toString() + " " +
                    startHourChoice.getValue() + ":" + startMinuteChoice.getValue() + ":00");
            // This converts endTime to Timestamp.
            Timestamp endTime = Timestamp.valueOf(endDatePicker.getValue().toString() + " " +
                    endHourChoice.getValue() + ":" + endMinuteChoice.getValue() + ":00");

            // Checks scheduling conflicts and errors.
            boolean checks;
            // This checks whether adding or updating appointments.
            if (!CustomerForm.addingAppointment) {
                // This will check for any scheduling issues.
                checks = Helper.updateAppointmentCheck(Integer.parseInt(appointmentIdTextField.getText()), startTime, endTime, JDBC.getCustomerId(customerCombo.getValue()));
                if (checks) {
                    JDBC.updateAppointment(selectedAppointment.getId(), appointmentTitleTextField.getText(), appointmentDescriptionTextField.getText(), appointmentLocationTextField.getText(),
                            appointmentTypeTextField.getText(), startTime, endTime, JDBC.getCustomerId(customerCombo.getValue()), JDBC.getUserId(userCombo.getValue()), JDBC.getContactId(contactCombo.getValue()));

                    Helper.errorDialog("Appointment updated");
                    Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/CustomerForm.fxml")));
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                }
            } else {
                // This will check for scheduling issues
                checks = Helper.addAppointmentCheck(startTime, endTime, JDBC.getCustomerId(customerCombo.getValue()));
                if (checks) {
                    JDBC.addAppointment(appointmentTitleTextField.getText(), appointmentDescriptionTextField.getText(), appointmentLocationTextField.getText(),
                            appointmentTypeTextField.getText(), startTime, endTime, JDBC.getCustomerId(customerCombo.getValue()), JDBC.getUserId(userCombo.getValue()), JDBC.getContactId(contactCombo.getValue()));

                    Helper.errorDialog("Appointment added.");
                    Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/CustomerForm.fxml")));
                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                }
            }
        }
    }

    /** This method will check that no fields are null
     * @return boolean false if there are empty inputs */
    public boolean checkInputs() {
        return !appointmentDescriptionTextField.getText().isEmpty() && !appointmentLocationTextField.getText().isEmpty() && !appointmentTitleTextField.getText().isEmpty() &&
                !appointmentTypeTextField.getText().isEmpty() && contactCombo.getValue() != null && customerCombo.getValue() != null && startDatePicker.getValue() != null &&
                endDatePicker.getValue() != null && startHourChoice.getValue() != null && startMinuteChoice.getValue() != null && endHourChoice.getValue() != null &&
                endMinuteChoice.getValue() != null;
    }

    /**
     * LAMBDA EXPRESSIONS: This will not allow users to enter appointment dates on weekends as well as dates
     * that have already passed. Instead of having to do separate checks it will disable the option to pick
     * dates outside of normal business hours.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // These are the hour and minute choiceBox values.
        hours.addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22");
        minutes.addAll("0", "15", "30", "45");

            startHourChoice.setItems(hours);
            endHourChoice.setItems(hours);
            startMinuteChoice.setItems(minutes);
            endMinuteChoice.setItems(minutes);


        // This will populate the ComboBox with contacts.
        ObservableList<String> contactNames = null;
        ObservableList<String> customerNames = null;
        ObservableList<String> userNames = null;
        try {
            contactNames = JDBC.getContactNames();
            customerNames = JDBC.getCustomerNames();
            userNames = JDBC.getUserNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        contactCombo.setItems(contactNames);
        customerCombo.setItems(customerNames);
        userCombo.setItems(userNames);

        // This will get the selected appointment information when editing the appointment.
        if (!CustomerForm.addingAppointment) {
            // Sets the inputs with the appointment that is selected.
            appointmentIdTextField.setText(Integer.toString(selectedAppointment.getId()));
            appointmentTitleTextField.setText(selectedAppointment.getTitle());
            appointmentDescriptionTextField.setText(selectedAppointment.getDescription());
            appointmentLocationTextField.setText(selectedAppointment.getLocation());
            appointmentTypeTextField.setText(selectedAppointment.getType());

            // This populates the start and end date pickers.
            startDatePicker.setValue(selectedAppointment.getStart().toLocalDateTime().toLocalDate());
            endDatePicker.setValue(selectedAppointment.getEnd().toLocalDateTime().toLocalDate());

            // This populates the start and end hour choice.
            startHourChoice.setValue(selectedAppointment.getStart().toLocalDateTime().toLocalTime().toString().substring(0,2)); // get hour
            startMinuteChoice.setValue(selectedAppointment.getStart().toLocalDateTime().toLocalTime().toString().substring(3,5)); // get minute
            endHourChoice.setValue(selectedAppointment.getEnd().toLocalDateTime().toLocalTime().toString().substring(0,2)); // get hour
            endMinuteChoice.setValue(selectedAppointment.getEnd().toLocalDateTime().toLocalTime().toString().substring(3,5)); // get minute

            /** LAMBDA EXPRESSION: Prevents users from choosing invalid dates. */
            startDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate startDatePicker, boolean empty) {
                    super.updateItem(startDatePicker, empty);
                    setDisable(
                            empty || startDatePicker.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                    startDatePicker.getDayOfWeek() == DayOfWeek.SUNDAY || startDatePicker.isBefore(LocalDate.now()));
                }
            });

            endDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate endDatePicker, boolean empty) {
                    super.updateItem(endDatePicker, empty);
                    setDisable(
                            empty || endDatePicker.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                    endDatePicker.getDayOfWeek() == DayOfWeek.SUNDAY || endDatePicker.isBefore(LocalDate.now()));
                }
            });

            try {
                contactCombo.setValue(JDBC.getContactName(selectedAppointment.getContact_id()));
                customerCombo.setValue(JDBC.getCustomerName(selectedAppointment.getCustomer_id()));
                userCombo.setValue(JDBC.getCurrentUserName(JDBC.getCurrentUser()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            appointmentIdTextField.setText("Auto-Generated");

            /** LAMBDA EXPRESSION: This will prevent users from choosing past dates.  */
            startDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate startDatePicker, boolean empty) {
                    super.updateItem(startDatePicker, empty);
                    setDisable(
                            empty || startDatePicker.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                    startDatePicker.getDayOfWeek() == DayOfWeek.SUNDAY || startDatePicker.isBefore(LocalDate.now()));
                }
            });

            endDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate endDatePicker, boolean empty) {
                    super.updateItem(endDatePicker, empty);
                    setDisable(
                            empty || endDatePicker.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                    endDatePicker.getDayOfWeek() == DayOfWeek.SUNDAY || endDatePicker.isBefore(LocalDate.now()));
                }
            });
        }
    }
}