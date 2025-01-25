package gov.iti.jets;

import java.io.IOException;

import gov.iti.jets.controller.AddContactController;
import gov.iti.jets.controller.ChatsController;
import gov.iti.jets.controller.ContactController;
import gov.iti.jets.controller.DashboardController;
import gov.iti.jets.controller.GroupsController;
import gov.iti.jets.controller.LoginPageController;
import gov.iti.jets.controller.RegistrationPageController;
import gov.iti.jets.controller.SettingsController;
import gov.iti.jets.controller.UserInfoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
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


        FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/screens/base.fxml"));
		BorderPane dashBoard = dashLoader.load();
        DashboardController dashController = dashLoader.getController();

        var dashScene = new Scene(dashBoard, width+200, height+100);

        dashController.setStage(stage);
        dashController.setLoginsScene(loginScene);

        FXMLLoader userInfoLoader = new FXMLLoader(getClass().getResource("/screens/User_Info.fxml"));
		BorderPane userInfo = userInfoLoader.load();
        UserInfoController userInfoController = userInfoLoader.getController();

        var userInfoScene = new Scene(userInfo, width-100, height+300);

        dashController.setuserInfoScene(userInfoScene);

        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/screens/Settings.fxml"));
		BorderPane settingsBoard = settingsLoader.load();
        SettingsController settingsController = settingsLoader.getController();

        var settingsScene = new Scene(settingsBoard, width+200, height+100);

        settingsController.setDashboardScene(dashScene);
        settingsController.setStage(stage);
        dashController.setSettingsScene(settingsScene);


        // FXMLLoader accountLoader = new FXMLLoader(getClass().getResource("/screens/AccountSettings.fxml"));
		// BorderPane accountBoard = accountLoader.load();
        // AccountController accountController = accountLoader.getController();

        // var accountScene = new Scene(accountBoard, width+200, height+100);

        // settingsController.setAccountScene(accountScene);
        // accountController.setDashboardScene(dashScene);
        // accountController.setSettingsScene(settingsScene);
        // accountController.setStage(stage);

        loginController.setDashScene(dashScene);
        registerController.setDashScene(dashScene);
        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}