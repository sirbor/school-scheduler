package Controllers;

import Helper.JDBC;
import Models.Appointment;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.Locale;
import java.util.Objects;

/**
 * This class contains helper methods such as displaying dialog as well as time conversions.
 */
abstract public class Helper {
    private static ZoneId timezone;

    /** Sets the Locale of the system
     * @return locale */
    private static Locale setLocale() {
        Locale locale = Locale.getDefault();
        if (Objects.equals(locale.getLanguage(), "fr")) {
            locale = new Locale("fr", "CA");
        } else {
            locale = new Locale("en", "US");
        }
        return locale;
    }

    /** Get system language
     * @return system language */
    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    /** Returns the systems country.
     * @return system country */
    public static String getCountry() {
        Locale locale = Locale.getDefault();
        return locale.getDisplayCountry();
    }

    /** Uses the user ID to get the username.
     * @return String username */
    public static String getUsernameFromId(int id) {
        return id == 1 ? "test" : "admin";
    }

    /** Uses the country ID to get the country name.
     * @return country name */
    public static String getCountryFromId(int id) {
        return switch (id) {
            case 1 -> "U.S";
            case 2 -> "UK";
            default -> "Canada";
        };
    }

    /** Returns setLocale() result.
     * @return system locale */
    public static Locale getLocale() {
        return setLocale();
    }

    /** This method creates an error dialog.
     * @param error message */
    public static void errorDialog(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error (Erreur)");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }

    /** This method creates a notification dialog.
     * @param notification message */
    public static void noticeDialog(String notification) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(notification);
        alert.showAndWait();
    }

    /**
     * Return timezone of system
     */
    public static ZoneId getTimeZone() {
        return timezone = ZoneId.systemDefault();
    }

    /**
     * Return Eastern Time Zone
     */
    public static ZoneId easternTime() {
        return timezone = ZoneId.of("America/New_York");
    }

    /**
     * Return UTC Time Zone
     */
    public static ZoneId utcTime() {
        return timezone = ZoneOffset.UTC;
    }

    /**
     * This method gets the users current time zone.
     * @return timezone
     */
    public static ZoneId getLocalTimezone() {
        return timezone;
    }

    /**
     * UTC to local time converter.
     * @param timestamp
     * @return timestamp local time
     */
    public static Timestamp toLocal(Timestamp timestamp) {
        return Timestamp.valueOf(timestamp.toLocalDateTime().atZone(
                ZoneId.of(utcTime().getId())).withZoneSameInstant(ZoneId.of(
                getLocalTimezone().getId())).toLocalDateTime());
        }

    /**
     * Eastern time to UTC converter.
     * @param timestamp
     * @return timestamp UTC time
     */
    public static Timestamp toUTC(Timestamp timestamp) { ;
        return timestamp;
    }

    /**
     * Local time to EST converter.
     * @param timestamp
     * @return
     */
    public static Timestamp localToEST(Timestamp timestamp) {
        return Timestamp.valueOf(timestamp.toLocalDateTime().atZone(
                ZoneId.of(getTimeZone().getId())).withZoneSameInstant(
                        ZoneId.of(easternTime().getId())).toLocalDateTime());
    }

    /**
     * Open reports dialog
     * @param reportType
     * @param reportBlurb
     * @param reportBody
     */
    public static void reportDialog(String reportType, String reportBlurb, String reportBody) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Report");
        alert.setHeaderText(reportType);
        alert.setContentText("Click 'Show Details' to view: " + reportBlurb);

        Label label = new Label("Report Output:");

        TextArea textArea = new TextArea(reportBody);
        textArea.setEditable(false);
        textArea.setWrapText(false); // Enable word wrapping for reports.

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set exception in the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * This method checks if there are appointments that overlap.
     * @param start
     * @param end
     * @return false if there is an overlap.
     */
    public static boolean mainCheck(Timestamp start, Timestamp end) {
        LocalDateTime appointmentStart = Helper.localToEST(start).toLocalDateTime();
        LocalDateTime appointmentEnd = Helper.localToEST(end).toLocalDateTime();
        LocalTime openTime = LocalTime.of(8,00);
        LocalTime closeTime = LocalTime.of(22,00);

        // Checks that start time is before end time
        if (appointmentStart.isAfter(appointmentEnd)) {
            if (LoginForm.language.equals("fr")) {
                // French dialog if language is set to French.
                Helper.errorDialog("L'heure de fin doit être postérieure à l'heure de début.");
            } else {
                Helper.errorDialog("The end time must be after the start time.");
            }
            return false;
        }

        // Checks that appointment start falls within office hours
        if (appointmentStart.toLocalTime().isBefore(openTime) ||
                appointmentStart.toLocalTime().isAfter(closeTime)) {
            if (LoginForm.language.equals("fr")) {
                Helper.errorDialog("Veuillez prendre rendez-vous pendant les heures de bureau.");
            } else {
                Helper.errorDialog("Please set appointment during office hours.");
            }
            return false;
        }

        // This will check that the appointment is set during business hours.
        if (appointmentEnd.toLocalTime().isBefore(openTime) ||
                appointmentEnd.toLocalTime().isAfter(closeTime)) {
            if (LoginForm.language.equals("fr")) {
                Helper.errorDialog("Le rendez-vous doit être pendant les heures de bureau.");
            } else {
                Helper.errorDialog("Appointment cannot end after office hours.");
            }
            return false;
        }

        // This checks that appointments aren't set up on the weekends.
        if (appointmentStart.toLocalDate().getDayOfWeek() == DayOfWeek.SATURDAY ||
                appointmentStart.toLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
            if (LoginForm.language.equals("fr")) {
                Helper.errorDialog("Le rendez-vous NE PEUT PAS être fixé le week-end.");
            } else {
                Helper.errorDialog("Appointment CANNOT be scheduled on the weekend.");
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method checks if appointments overlaps.
     * @param start
     * @param end
     * @param customer_id
     * @return false if there is an overlap.
     * @throws SQLException
     */
    public static boolean addAppointmentCheck(Timestamp start, Timestamp end, int customer_id) throws SQLException {
        LocalDateTime appointmentStart = Helper.localToEST(start).toLocalDateTime();
        LocalDateTime appointmentEnd = Helper.localToEST(end).toLocalDateTime();

        if (!mainCheck(start, end)) {
            return false;
        }

        else if (mainCheck(start, end)) {
            FilteredList<Appointment> customerAppointments = JDBC.getAppointments().filtered(
                    appointment -> appointment.getCustomer_id() == customer_id);

            for (Appointment appointment : customerAppointments) {
                LocalDateTime apptStart = Helper.localToEST(appointment.getStart()).toLocalDateTime();
                LocalDateTime apptEnd = Helper.localToEST(appointment.getEnd()).toLocalDateTime();

                if (!overlapCheck(appointmentStart, appointmentEnd, apptStart, apptEnd, appointment.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method checks for overlap when updating an appointment.
     * @param id
     * @param start
     * @param end
     * @param customer_id
     * @return false if there is an overlap.
     * @throws SQLException
     */
    public static boolean updateAppointmentCheck(int id, Timestamp start, Timestamp end, int customer_id) throws SQLException {
        LocalDateTime appointmentStart = Helper.localToEST(start).toLocalDateTime();
        LocalDateTime appointmentEnd = Helper.localToEST(end).toLocalDateTime();
        //boolean passed = mainCheck(start, end);
        /*FilteredList<Appointment> customerAppointments = JDBC.getAppointments().filtered(
                appointment -> appointment.getCustomer_id() == customer_id); */

        if (!mainCheck(start, end)) {
            return false;
        } else if (mainCheck(start, end)) {
            FilteredList<Appointment> customerAppointments = JDBC.getAppointments().filtered(
                    appointment -> appointment.getCustomer_id() == customer_id);

            for (Appointment a : customerAppointments) {
                LocalDateTime apptStart = Helper.localToEST(a.getStart()).toLocalDateTime();
                LocalDateTime apptEnd = Helper.localToEST(a.getEnd()).toLocalDateTime();
                if (a.getId() != id) {
                    if (!overlapCheck(appointmentStart, appointmentEnd, apptStart, apptEnd, a.getId())) {
                        return false;
                    }
                } else {
                    return true;
                }
            }
            return true;
        }
        return true;
    }

    /**
     * This checks if there is an overlap in appointments.
     * @param appointmentStart
     * @param appointmentEnd
     * @param apptStart
     * @param apptEnd
     * @param apptId
     * @return false if there is an overlap.
     */
    public static boolean overlapCheck(LocalDateTime appointmentStart, LocalDateTime appointmentEnd,
                                       LocalDateTime apptStart, LocalDateTime apptEnd, int apptId) {

        // Check overlap and create dialog if overlap is detected.
        if (appointmentStart.isBefore(apptStart) && appointmentEnd.isAfter(apptEnd)) {
            if (LoginForm.language.equals("fr")) {
                Helper.errorDialog("Il y a un chevauchement de rendez-vous. " + apptId + ".");
            } else {
                Helper.errorDialog("There is an appointment overlap. " + apptId + ".");
            }
            return false;
        }

        // This checks for start appointment overlap.
        else if (appointmentStart.isAfter(apptStart) && appointmentStart.isBefore(apptEnd)) {
            if (LoginForm.language.equals("fr")) {
                Helper.errorDialog("Il y a un chevauchement de rendez-vous au début du rendez-vous. " + apptId + ".");
            } else {
                Helper.errorDialog("There is an appointment overlap at the start of the appointment. " + apptId + ".");
            }
            return false;
        }

        // This check for an overlap at the end of the appointment.
        else if (appointmentEnd.isAfter(apptStart) && appointmentEnd.isBefore(apptEnd)) {
            if (LoginForm.language.equals("fr")) {
                Helper.errorDialog("Il y a un chevauchement de rendez-vous à la fin du rendez-vous. " + apptId + ".");
            } else {
                Helper.errorDialog("There is an appointment overlap at the end of the appointment. " + apptId + ".");
            }
            return false;
        }

        // This checks appointment start time conflicts.
        else if (appointmentStart.equals(apptStart)) {
            if (LoginForm.language.equals("fr")) {
                Helper.errorDialog("Ce rendez-vous commence en même temps que le rendez-vous " + apptId + ".");
            } else {
                Helper.errorDialog("This appointment starts at the same time as appointment " + apptId + ".");
            }
            return false;
        }

        // This checks for appointment end time conflicts.
        else if (appointmentEnd.equals(apptEnd)) {
            if (LoginForm.language.equals("fr")) {
                Helper.errorDialog("Ce rendez-vous se termine en même temps que le rendez-vous " + apptId + ".");
            } else {
                Helper.errorDialog("This appointment ends at the same time as appointment " + apptId + ".");
            }
            return false;
        } else {
            return true;
        }
    }
}


