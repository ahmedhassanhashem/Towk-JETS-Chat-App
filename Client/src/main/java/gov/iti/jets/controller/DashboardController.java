package gov.iti.jets.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class DashboardController {


    private Stage stage;
    private Scene LoginScene;
    private Scene settingsScene;
    private Scene userInfoScene;
    @FXML
    private BorderPane borderPane;

    public void setLoginsScene(Scene s){
        LoginScene = s;
    }

    public void setSettingsScene(Scene s){
        settingsScene = s;
    }

    public void setuserInfoScene(Scene s){
        userInfoScene = s;
    }





    public void setStage(Stage s){
        stage =s;
    }

    @FXML
    private void contacts(MouseEvent event){
        // System.out.println("aa");
        BorderPane hold =null;
        FXMLLoader contactLoader = new FXMLLoader(getClass().getResource("/screens/base1.fxml"));

    try {
        hold = contactLoader.load();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    ChatsController c = contactLoader.getController();
    c.contactScene();
    borderPane.setCenter(hold);
    }

    @FXML
    private void chats(MouseEvent event){
        BorderPane hold =null;
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/base1.fxml"));

    try {
        hold = chatLoader.load();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    ChatsController c = chatLoader.getController();
    c.chatScene();
    borderPane.setCenter(hold);

    }

    @FXML
    private void userInfo(MouseEvent event){
        Stage info = new Stage();
        info.initOwner(stage);
        info.initModality(Modality.APPLICATION_MODAL);
        info.setScene(userInfoScene);
        info.show();
    }

    @FXML
    private void settings(MouseEvent event){
        // System.out.println("aa");
        stage.setScene(settingsScene);
    }
    
    @FXML
    private void groups(MouseEvent event){
        BorderPane hold =null;
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/base1.fxml"));

    try {
        hold = chatLoader.load();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    ChatsController c = chatLoader.getController();
    c.groupScene();
    borderPane.setCenter(hold);
    }

    @FXML
    private void notifications(MouseEvent event){
        BorderPane hold =null;
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
    private void announcements(MouseEvent event){
        BorderPane hold =null;
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
    private void signOut(MouseEvent event){
        stage.setScene(LoginScene);
    }

    @FXML
    private void initialize() {
        BorderPane hold =null;
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
