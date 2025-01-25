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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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

public class ContactTabController {

    private int pageCounter = 1;

    
    private TabPane tabPane;

    public void setTabPane(TabPane t){
        tabPane = t;
    }
    public void setPageCounter(int x){
        pageCounter =x;
        tab.setText("Contact #"+pageCounter);
    }
    @FXML
    private Tab tab;
    // public void setGroupsSceneScene(Scene s){
    //     groupsScene = s;
    // }
    // public void setuserInfoScene(Scene s){
    //     userInfoScene = s;
    // }

    // public void setchatsScene(Scene s){
    //     chatsScene = s;
    // }

    // public void setcontactScene(Scene s){
    //     contactScene = s;
    // }


    // @FXML
    // private void addContact(MouseEvent event){
    //     // System.out.println("aa");
    //     stage.setScene(addContactScene);
    // }

    @FXML
    private void newContact(ActionEvent event){
        Tab tab2 = null;
        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/screens/ContactTab.fxml"));
		try {
            tab2 = addContactLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContactTabController addContactController = addContactLoader.getController();
        addContactController.setTabPane(tabPane);
        addContactController.setPageCounter(pageCounter+1);
        tabPane.getTabs().add(tab2);
        tabPane.getSelectionModel().select(tab2);
        
    }
    
    // @FXML
    // private void contacts(MouseEvent event){
        //     // System.out.println("aa");
        //     stage.setScene(contactScene);
        // }
        
        // @FXML
        // private void userInfo(MouseEvent event){
            //     Stage info = new Stage();
            //     info.initOwner(stage);
            //     info.initModality(Modality.APPLICATION_MODAL);
            //     info.setScene(userInfoScene);
            //     info.show();
            // }
            
            // @FXML
            // private void settings(MouseEvent event){
                //     // System.out.println("aa");
                //     stage.setScene(settingsScene);
                // }
                
                // @FXML
                // private void groups(MouseEvent event){
                    //     stage.setScene(groupsScene);
                    // }
                    
                    @FXML
                    private void initialize() {
                        
                    }
}
