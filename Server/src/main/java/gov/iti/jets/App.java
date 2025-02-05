package gov.iti.jets;

import java.io.IOException;

import gov.iti.jets.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // stage.setTitle("Browsar");

        int width = 640,height = 480;


        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/screens/Login.fxml"));
		GridPane rootLogin = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        
        Scene loginScene = new Scene(rootLogin, width, height);

        loginController.setStage(stage);


        // var scene = new Scene(loginScene, width, height);


        // stage.setResizable(false);
        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}