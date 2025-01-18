package gov.iti.jets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;




import java.io.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class AccountController {


    private Stage stage;
    private Scene settingsScene;
    private Scene dashboardScene;


    public void setSettingsScene(Scene s){
        settingsScene = s;
    }

    public void setDashboardScene(Scene s){
        dashboardScene = s;
    }

    public void setStage(Stage s){
        stage =s;
    }
    @FXML
    private void settings(ActionEvent event){
        // System.out.println("aa");
        stage.setScene(settingsScene);
    }
    @FXML
    private void dashboard(ActionEvent event){
        // System.out.println("aa");
        stage.setScene(dashboardScene);
    }




    @FXML
    private void initialize() {

    }
}
