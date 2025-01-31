package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import gov.iti.jets.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DashboardController {

    private Stage stage;
    private Scene LoginScene;
    private Scene settingsScene;
    private Scene userInfoScene;
    private Scene dashScene;
    private UserDTO userDTO = new UserDTO();
    private ScheduledExecutorService scheduledExecutorService;
    ChatsController chat;
    @FXML
    private Label nameLabel;

    @FXML
    private ImageView profileImage;

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
        if (user.getUserPicture() != null) {
            // System.out.println(user.getUserPicture().length);
            ByteArrayInputStream bis = new ByteArrayInputStream(user.getUserPicture());
            Image image = new Image(bis);
            profileImage.setImage(image);
        }
        chat.setUserDTO(userDTO);
        chat.chatScene();
    }

    public void setDashScene(Scene l) {
        dashScene = l;
    }

    public void setStage(Stage s) {
        stage = s;
        scheduledExecutorService=
Executors. newScheduledThreadPool(20);
        chat.setScheduledExecutorService(scheduledExecutorService);
        chat.setStage(s);
        try {
            
            stage.setOnCloseRequest((e)->{
                // scheduledExecutorService.close();
                scheduledExecutorService.shutdownNow();
                
            });
        } catch (Exception e) {
        }
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
        c.setStage(stage);
        c.setScheduledExecutorService(scheduledExecutorService);
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
        c.setStage(stage);
        c.setScheduledExecutorService(scheduledExecutorService);
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
            settingsController.setUserDTO(userDTO);
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
        c.setScheduledExecutorService(scheduledExecutorService);
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
        int width = 640, height = 480;
        FXMLLoader dashLoader = new FXMLLoader(getClass().getResource("/screens/loginP.fxml"));
        VBox dashBoard = null;
        try {
            dashBoard = dashLoader.load();
        } catch (IOException ex) {
        }
        LoginPController dashController = dashLoader.getController();

        var dashScene = new Scene(dashBoard, width, height);
        dashController.setStage(stage);
        dashController.setNameField(userDTO.getName());
        if(userDTO.getUserPicture() !=null)
        dashController.setProfileImage(userDTO.getUserPicture());
        userDTO.setPassword(null);
        dashController.setUdto(userDTO);
        
        stage.setScene(dashScene);
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
        chat = chatLoader.getController();
        


    }
}
