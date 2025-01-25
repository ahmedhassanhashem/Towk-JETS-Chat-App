package gov.iti.jets.controller;

import java.io.IOException;

import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DashboardController {

    private Stage stage;
    private Scene LoginScene;
    private Scene settingsScene;
    private Scene userInfoScene;
    private Scene dashScene;
    private UserDTO userDTO = new UserDTO();

    @FXML
    private Label nameLabel;

    @FXML
    private BorderPane borderPane;

    public void setLoginsScene(Scene s) {
        LoginScene = s;
    }

    public void setSettingsScene(Scene s) {
        settingsScene = s;
    }

    public void setuserInfoScene(Scene s) {
        userInfoScene = s;
    }

    public void setUserDTO(UserDTO user) {
        userDTO = user;
        nameLabel.setText(user.getName());

    }

    public void setDashScene(Scene l) {
        dashScene = l;
    }

    public void setStage(Stage s) {
        stage = s;
    }

    @FXML
    private void contacts(MouseEvent event) {
        // System.out.println("aa");
        BorderPane hold = null;
        FXMLLoader contactLoader = new FXMLLoader(getClass().getResource("/screens/base1.fxml"));

        try {
            hold = contactLoader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ChatsController c = contactLoader.getController();
        c.setUserDTO(userDTO);
        c.contactScene();
        borderPane.setCenter(hold);
    }


    

    @FXML
    private void chats(MouseEvent event) {
        BorderPane hold = null;
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/base1.fxml"));

        try {
            hold = chatLoader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ChatsController c = chatLoader.getController();
        c.setUserDTO(userDTO);
        c.chatScene();
        borderPane.setCenter(hold);

    }

    @FXML
    private void userInfo(MouseEvent event) {
        Stage info = new Stage();
        info.initOwner(stage);
        info.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader userInfoLoader = new FXMLLoader(getClass().getResource("/screens/User_Info.fxml"));
        BorderPane userInfo;
        try {
            userInfo = userInfoLoader.load();
            UserInfoController userInfoController = userInfoLoader.getController();

            var userInfoScene = new Scene(userInfo, 500, 800);
            info.setScene(userInfoScene);
            info.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void settings(MouseEvent event) {
        // System.out.println("aa");
        FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/screens/Settings.fxml"));
        BorderPane settingsBoard;
        try {
            settingsBoard = settingsLoader.load();
            SettingsController settingsController = settingsLoader.getController();

            var settingsScene = new Scene(settingsBoard, 640 + 200, 480 + 100);

            settingsController.setDashboardScene(dashScene);
            settingsController.setStage(stage);
            stage.setScene(settingsScene);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    private void groups(MouseEvent event) {
        BorderPane hold = null;
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/base1.fxml"));

        try {
            hold = chatLoader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ChatsController c = chatLoader.getController();
        c.setUserDTO(userDTO);
        c.groupScene();
        borderPane.setCenter(hold);
    }

    @FXML
    private void notifications(MouseEvent event) {
        BorderPane hold = null;
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/NotificationBase.fxml"));

        try {
            hold = chatLoader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        borderPane.setCenter(hold);
    }

    @FXML
    private void announcements(MouseEvent event) {
        BorderPane hold = null;
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/AnnouncementBase.fxml"));

        try {
            hold = chatLoader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        borderPane.setCenter(hold);
    }

    @FXML
    private void signOut(MouseEvent event) {
        stage.setScene(LoginScene);
    }

    @FXML
    private void initialize() {
        BorderPane hold = null;
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/base1.fxml"));

        try {
            hold = chatLoader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        borderPane.setCenter(hold);
        ChatsController c = chatLoader.getController();
        c.chatScene();
    }
}
