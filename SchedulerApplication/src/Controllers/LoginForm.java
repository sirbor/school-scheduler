package Controllers;

import Helper.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * This class contains methods that control the login form behaviour.
 */
public class LoginForm extends Helper implements Initializable {
    @FXML private Label zoneLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Button loginButton;
    public static final String language = Helper.getLanguage();
    public static boolean initialLogon = true;

    /**
     * This method authenticates username and password. Logs all login attempts to a txt file.
     * @param event
     * @throws Exception
     */
    @FXML private void handleLogin(ActionEvent event) throws Exception {
    final String userName = usernameTextField.getText();
    final String password = passwordTextField.getText();

    if ((userName.length() != 0) && (password.length() != 0)) {
        // Used to check the database for username and password combination.
        boolean match = JDBC.checkLogin(userName, password);
        if (match) {
            loginTracker(true);
            Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("..\\Views\\customerForm.fxml"))));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } else {
            loginTracker(false);
            // Pop-up dialog if username or password is incorrect.
            if (language.equals("fr")) {
                Helper.errorDialog("Le nom d'utilisateur/mot de passe était incorrect.");
            } else {
                Helper.errorDialog("Username/Password was incorrect.");
            }
        }
    } else {
        if (language.equals("fr")) {
            Helper.errorDialog("Le nom d'utilisateur et le mot de passe sont requis.");
        } else {
            Helper.errorDialog("Username and password are required.");
        }

        }
    }

    /**
     * Logs every login attempt to login_activity.txt.
     * @param loggedIn
     * @throws Exception
     */
    private void loginTracker(boolean loggedIn) throws Exception {
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File("login_activity.txt"), true));
        pw.append("\nLogin attempt: ").append(String.valueOf(ZonedDateTime.of(LocalDateTime.now(), Helper.getLocalTimezone()))).append("\nUsername: ").append(usernameTextField.getText()).append("\nSuccessful: ").append(String.valueOf(loggedIn)).append("\n");
        pw.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Helper.getTimeZone();
        if (Helper.getLanguage().equals("fr")) {
            usernameLabel.setText("Nom d'utilisateur");
            passwordLabel.setText("Mot de passe");
            loginButton.setText("Connexion");
        }
        // Outputs TimeZone information to label
        zoneLabel.setText("Zone ID: " + Helper.getLocalTimezone().toString() + "\nTimeZone: " + TimeZone.getDefault().getDisplayName());
    }
}
