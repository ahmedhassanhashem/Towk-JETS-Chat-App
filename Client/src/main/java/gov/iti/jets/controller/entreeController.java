package gov.iti.jets.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class entreeController {

    private Stage stage;
    // private UserDAO userDao = new UserDAO();
        // private UserDAOInterface userDAO;

    private Scene myScene;

    VBox rootRegister =null;
    VBox rootLogin = null;
    RegistrationPageController registerController;
    LoginPageController loginController;
    
    public void setMyScene(Scene s){
        myScene = s;
        loginController.setLoginsScene(s);
        registerController.setLoginsScene(s);
    }

    @FXML
    private BorderPane borderPane;

    @FXML
    private Button signupButton;

    @FXML
    private Button signButton;

    @FXML
    private void gotoSingin() {
        // stage.setScene(signup);
        borderPane.setCenter(rootLogin);

    }

    public void setStage(Stage stage) {
        this.stage = stage;
        // System.out.println(stage);
        loginController.setStage(stage);
        registerController.setStage(stage);

    }

    @FXML
    private void gotoSingup() {
        borderPane.setCenter(rootRegister);
        
    }


    // @FXML
    // private ListView<FlowPane> list;

    // @FXML
    // private TextField txtF;

    // private TreeItem<FlowPane> allroot;
    // @FXML
    // private void onEnter(ActionEvent event){

    // }

    @FXML
    private void initialize() {
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/screens/Register.fxml"));
		 try {
            rootRegister = registerLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        registerController = registerLoader.getController();

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/screens/login.fxml"));
		 try {
            rootLogin = loginLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginController = loginLoader.getController();


        borderPane.setCenter(rootLogin);



}
}
