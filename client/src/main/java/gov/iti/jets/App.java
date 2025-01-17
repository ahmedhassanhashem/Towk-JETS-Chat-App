package gov.iti.jets;

import java.io.IOException;

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
        stage.setTitle("Browsar");

        int width = 640,height = 480;

        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/Registration Page.fxml"));
		GridPane rootLogin = registerLoader.load();
        RegistrationPageController registerController = registerLoader.getController();

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/Login Page.fxml"));
		GridPane rootRegister = loginLoader.load();
        LoginPageController loginController = loginLoader.getController();
        
        Scene loginScene = new Scene(rootLogin, width, height);
        Scene registerScene = new Scene(rootRegister, width, height);

        loginController.setStage(stage);
        loginController.setSignUp(loginScene);

        registerController.setStage(stage);
        registerController.setSignIn(registerScene);

        FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/DashBoard.fxml"));
		GridPane dashBoard = dashLoader.load();
        DashboardController dashController = dashLoader.getController();

        var dashScene = new Scene(dashBoard, width+200, height+100);

        FXMLLoader contactLoader = new FXMLLoader(getClass().getResource("/Contact.fxml"));
		GridPane contactBoard = contactLoader.load();
        ContactController contactController = contactLoader.getController();

        var contactScene = new Scene(contactBoard, width+200, height+100);

        dashController.setStage(stage);
        dashController.setcontactScene(contactScene);
        contactController.setStage(stage);
        contactController.setchatsScene(dashScene);

        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/AddNewContacts.fxml"));
		GridPane addContactBoard = addContactLoader.load();
        AddContactController addContactController = addContactLoader.getController();

        var addContactScene = new Scene(addContactBoard, width+200, height+100);
        contactController.setAddContactScene(addContactScene);
        dashController.setAddContactScene(addContactScene);
        loginController.setdashScene(dashScene);

        addContactController.setStage(stage);
        addContactController.setchatsScene(dashScene);
        addContactController.setcontactScene(contactScene);

        registerController.setdashScene(dashScene);
        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}