package gov.iti.jets.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.UserDTO;


public class LoginPageController {

    private Stage stage;
    private Scene signup;
    private Scene dashScene;
    private UserDAO userDao = new UserDAO();

    @FXML
    private Button loginButton;


    @FXML
    private Label invalid;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void gotoSignup() {
        stage.setScene(signup);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        var w = stage.widthProperty().multiply(0.3);
        phoneField.prefWidthProperty().bind(w);
        passwordField.prefWidthProperty().bind(w);
    }

    @FXML
    private void signIn(ActionEvent event) {
        
        stage.setScene(dashScene);
        /*
                UserDTO user = new UserDTO();
        user.setPhone(phoneField.getText());
        user.setPassword(passwordField.getText());
        user = userDao.read(user);
        if(user != null)
        Platform.runLater(() -> stage.setScene(dashScene));
        else{
            Platform.runLater(() ->{
            invalid.setVisible(true);
            phoneField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            passwordField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
            System.out.println("Wrong user/pass");
        });
        }
        */
    }

    public void setDashScene(Scene s) {
        
        dashScene = s;
    }

    public void setSignUp(Scene s) {
        signup = s;
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
