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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gov.iti.jets.dto.UserDTO;

public class SettingsController {

    private UserDTO userDTO = new UserDTO();
    
    
    private Stage stage;
    private Scene accountScene;
    private Scene LoginScene;
    private Scene dashboardScene;
    private VBox profile =null;
    private VBox account =null;
    private ProfileSettingsController controllerProfile;
    private AccountSettingsController controllerAccount;

    public void setUserDTO(UserDTO user) {
        userDTO = user;
        if (controllerProfile != null) {
            controllerProfile.setUserDTO(user);
        }
        if (controllerAccount != null) {
            controllerAccount.setUserDTO(user);
        }
    }

    public void setLoginsScene(Scene s) {
        LoginScene = s;
    }

    @FXML
    private BorderPane borderPane;

    public void setAccountScene(Scene s){
        accountScene = s;
    }

    public void setDashboardScene(Scene s){
        dashboardScene = s;
    }

    public void setStage(Stage s){
        stage =s;
    }

    @FXML
    private void accountButton(ActionEvent event){
        // System.out.println("aa");
        // stage.setScene(accountScene);
        borderPane.setCenter(account);
    }

    @FXML
    private void profileButton(ActionEvent event){
        // System.out.println("aa");
        // stage.setScene(accountScene);
        borderPane.setCenter(profile);
    }
    
    @FXML
    private void dashboard(ActionEvent event){
        // System.out.println("aa");
        stage.setScene(dashboardScene);
    }

    @FXML
    private void backButton(ActionEvent event){
        // System.out.println("aa");
        stage.setScene(dashboardScene);
    }



    @FXML
    private void initialize() {
        FXMLLoader profileSettingsLoader = new FXMLLoader(getClass().getResource("/screens/ProfileSettings.fxml"));

    try {
        profile = profileSettingsLoader.load();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    ProfileSettingsController c = profileSettingsLoader.getController();

    FXMLLoader accountSettingsLoader = new FXMLLoader(getClass().getResource("/screens/AccountSettings.fxml"));

    try {
        account = accountSettingsLoader.load();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    AccountSettingsController c2 = accountSettingsLoader.getController();

    borderPane.setCenter(account);

    controllerProfile = profileSettingsLoader.getController();
    controllerAccount = accountSettingsLoader.getController();
    
    if (userDTO != null) {
        controllerProfile.setUserDTO(userDTO);
        controllerAccount.setUserDTO(userDTO);
        controllerProfile.setStage(stage);
        controllerAccount.setStage(stage);
        

    }
    if(userDTO.getUserPicture() != null)
            controllerProfile.image.setImage(new Image(new ByteArrayInputStream(userDTO.getUserPicture())));

    }

}

