package gov.iti.jets;

import java.io.IOException;

import gov.iti.jets.controller.LoginController;
import gov.iti.jets.controller.LoginPageController;
import gov.iti.jets.controller.RegistrationPageController;
import gov.iti.jets.controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
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



        FXMLLoader serveLoader= new FXMLLoader(getClass().getResource("/screens/Server.fxml"));
        GridPane server = serveLoader.load();
        ServerController serverController = serveLoader.getController();
        serverController.setStage(stage);
        serverController.setLogin(loginScene);

        Scene serverScene = new Scene(server, width+200, height+20);
        loginController.setServerScene(serverScene);
        // stage.setResizable(false);
        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}