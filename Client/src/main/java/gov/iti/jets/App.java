package gov.iti.jets;

import java.io.IOException;

import gov.iti.jets.controller.DashboardController;
import gov.iti.jets.controller.LoginPageController;
import gov.iti.jets.controller.RegistrationPageController;
import gov.iti.jets.controller.SettingsController;
import gov.iti.jets.controller.UserInfoController;
import gov.iti.jets.controller.entreeController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
        FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/screens/entreeBase.fxml"));
		GridPane dashBoard = dashLoader.load();
        entreeController dashController = dashLoader.getController();

        var dashScene = new Scene(dashBoard, width, height);
        dashController.setStage(stage);
        stage.setScene(dashScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}