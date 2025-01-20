package gov.iti.jets;

import java.io.IOException;


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

        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/screens/Registration Page.fxml"));
		GridPane rootRegister = registerLoader.load();
        RegistrationPageController registerController = registerLoader.getController();

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/screens/Login Page.fxml"));
		GridPane rootLogin = loginLoader.load();
        LoginPageController loginController = loginLoader.getController();
        
        Scene loginScene = new Scene(rootLogin, width, height);
        Scene registerScene = new Scene(rootRegister, width, height);

        loginController.setStage(stage);
        loginController.setSignUp(registerScene);

        registerController.setStage(stage);
        registerController.setSignIn(loginScene);

        // var scene = new Scene(loginScene, width, height);



        FXMLLoader serveLoader= new FXMLLoader(getClass().getResource("/screens/Server.fxml"));
        GridPane server = serveLoader.load();
        ServerController serverController = serveLoader.getController();
        serverController.setStage(stage);
        serverController.setLogin(loginScene);

        Scene serverScene = new Scene(server, width+200, height+20);
        loginController.setServerScene(serverScene);
        registerController.setServerScene(serverScene);
        stage.setResizable(false);
        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}