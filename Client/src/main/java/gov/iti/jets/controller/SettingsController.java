package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import gov.iti.jets.dto.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SettingsController {

    private UserDTO userDTO = new UserDTO();

    private Stage stage;
    private Scene accountScene;
    private Scene LoginScene;
    private Scene dashboardScene;
    private VBox profile = null;
    private VBox account = null;
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

    public void setAccountScene(Scene s) {
        accountScene = s;
    }

    public void setDashboardScene(Scene s) {
        dashboardScene = s;
    }

    public void setStage(Stage s) {
        stage = s;
        if (controllerAccount != null) {
            controllerAccount.setStage(s);
        }
        if (controllerProfile != null) {
            controllerProfile.setStage(s);
        }
    }

    @FXML
    private void accountButton(ActionEvent event) {
        // System.out.println("aa");
        // stage.setScene(accountScene);
        borderPane.setCenter(account);
    }

    @FXML
    private void profileButton(ActionEvent event) {
        // System.out.println("aa");
        // stage.setScene(accountScene);
        borderPane.setCenter(profile);
    }

    @FXML
    private void dashboard(ActionEvent event) {
        // System.out.println("aa");
        stage.setScene(dashboardScene);
    }

    @FXML
    private void backButton(ActionEvent event) {
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

        borderPane.setCenter(account);

        controllerProfile = profileSettingsLoader.getController();
        controllerAccount = accountSettingsLoader.getController();

        if (userDTO != null) {
            controllerProfile.setUserDTO(userDTO);
            controllerAccount.setUserDTO(userDTO);
            controllerProfile.setStage(stage);
            controllerAccount.setStage(stage);

        }
        if (userDTO.getUserPicture() != null) {
            controllerProfile.image.setImage(new Image(new ByteArrayInputStream(userDTO.getUserPicture())));
        }

    }

}
