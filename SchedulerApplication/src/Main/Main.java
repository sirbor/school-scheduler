package Main;

import Helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;



/**
 * @author Henry Ventura
 * Main Class
 */

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("..\\Views\\loginForm.fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args){
        JDBC.openConnection(); // Opens DB connection
        launch(args);
        JDBC.closeConnection(); // Closes DB connection
    }
}
