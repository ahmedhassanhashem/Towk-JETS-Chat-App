package gov.iti.jets.controller;


import java.io.IOException;

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.dao.AdminDAO;
import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.UserDTO;

public class LoginController {

    private Stage stage;
    private AdminDAO userDao;

    @FXML
    private Button loginButton;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label invalid;

    public void setStage(Stage stage) {
        this.stage = stage;
        // ReadOnlyDoubleProperty h = stage.heightProperty();
        var w = stage.widthProperty().multiply(0.3);
        phoneField.prefWidthProperty().bind(w);
        passwordField.prefWidthProperty().bind(w);

        // invalid.minWidthProperty().bind(w);
        // invalid.minHeightProperty().bind(h);
    }

    @FXML
    private void signIn(ActionEvent event) {
        // System.out.println("aa");
        UserDTO user = new UserDTO();
        user.setPhone(phoneField.getText());
        user.setPassword(passwordField.getText());

            user = userDao.read(user);
    
        if(user != null){
            try {
                 int ret = userDao.updateFirstLogin(user.getUserID());
                 int width = 640,height = 480;
                 if(ret ==0){
                     FXMLLoader serveLoader= new FXMLLoader(getClass().getResource("/screens/Server.fxml"));
                     GridPane server = serveLoader.load();
                     ServerController serverController = serveLoader.getController();
                     serverController.setStage(stage);
                     Scene serverScene = new Scene(server, width+200, height+20);
                     Platform.runLater(() -> stage.setScene(serverScene));
                    }else{
                        FXMLLoader settingsLoader= new FXMLLoader(getClass().getResource("/screens/AccountSettings.fxml"));
                        VBox settings = settingsLoader.load();
                        AccountSettingsController settingsController = settingsLoader.getController();
                        settingsController.setStage(stage);
                        settingsController.setUserDTO(user);
                        Scene settingsrScene = new Scene(settings, width, height);
                        Platform.runLater(() -> stage.setScene(settingsrScene));
                    }
            } catch (IOException ex) {
                ex.printStackTrace();;
            }
        }
        // stage.setScene(serverScene);
        else{
            Platform.runLater(() ->{
            invalid.setVisible(true);
            phoneField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            System.out.println("Wrong user/pass");
        });
        }
    }


    @FXML
    private void initialize() {

            userDao = new AdminDAO();

        
        // phoneField.onKeyTypedProperty(()-> invalid.setVisible(false));
        phoneField.setOnKeyPressed((e)-> {
            
            invalid.setVisible(false);
            phoneField.setBorder(null);   
            passwordField.setBorder(null);   
        });
        passwordField.setOnKeyPressed((e)-> {
            invalid.setVisible(false);
            phoneField.setBorder(null);   
            passwordField.setBorder(null); 
        });
        loginButton.sceneProperty().addListener((observable, oldScene, newScene)-> {
            if(newScene !=null) setSaveAccelerator(loginButton);
            });
        

    }
    private void setSaveAccelerator(Button button) {
        if(button==null) {
            System.out.println("Button is null! "); 
        }
        Scene scene = button.getScene();
        if (scene == null) {
            throw new IllegalArgumentException("setSaveAccelerator must be called when a button is attached to a scene");
        }

       scene.getAccelerators().put(
            new KeyCodeCombination(KeyCode.ENTER),
            new Runnable() {
                @FXML public void run() {

                    button.fire();
                }
            }
       );
   }
}
