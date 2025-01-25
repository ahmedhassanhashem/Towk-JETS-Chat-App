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
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.PieChart;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
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

import gov.iti.jets.dao.UserDAO;

public class ServerController {

    private Stage stage;
    private BorderPane manage;
    private BorderPane announce;
    private BorderPane chart;
    private Scene login;
    // private Scene signup;
    
    @FXML
    private AnchorPane anchor;
    @FXML
    private BorderPane borderPane;

    @FXML
    private void signOut(ActionEvent event){
        stage.setScene(login);
    }
    // @FXML
    // private void gotoSingup(){
    //     stage.setScene(signup);
    // }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setLogin(Scene login){
        this.login = login;
    }
    // public void setSignUp(Scene s){
    //     signup = s;
    // }

    // @FXML
    // private ListView<FlowPane> list;

    // @FXML
    // private TextField txtF;

    // private TreeItem<FlowPane> allroot;
    @FXML
    private void manageButton(ActionEvent event){
    //    borderPane.setCenter(manage.getCenter());
    //    borderPane.setTop(manage.getTop());
    anchor.getChildren().clear();
    AnchorPane.setTopAnchor(manage, 0.0);
    AnchorPane.setBottomAnchor(manage, 0.0);
    AnchorPane.setLeftAnchor(manage, 0.0);
    AnchorPane.setRightAnchor(manage, 0.0);
    anchor.getChildren().add(manage);
    }

    @FXML
    private void announceButton(ActionEvent event){
        anchor.getChildren().clear();
        AnchorPane.setTopAnchor(announce, 0.0);
        AnchorPane.setBottomAnchor(announce, 0.0);
        AnchorPane.setLeftAnchor(announce, 0.0);
        AnchorPane.setRightAnchor(announce, 0.0);
        anchor.getChildren().add(announce);
        // System.out.println(anchor.getHeight());
        // System.out.println(anchor.getWidth());
        // System.out.println(announce.getHeight());
        // System.out.println(announce.getWidth());
    }

    @FXML
    private void status(ActionEvent event){
    anchor.getChildren().clear();

        // borderPane.setCenter(null);

        // Label lbl = (Label)borderPane.getTop();
        // lbl.setText("Status Statistics");

        PieChart pie = (PieChart)chart.getCenter();
        pie.setTitle("Status");
        UserDAO user = new UserDAO();
        ObservableList<PieChart.Data> pieChartData = user.getUserStatistics("userStatus");
        pie.setData(pieChartData);
        pie.setLegendSide(Side.BOTTOM);
        pie.setLabelLineLength(20);
        // pie.setAnimated(true);
        // pie.setClockwise(true); 
        // pie.setLabelLineLength(50); 
        pie.setLegendVisible(true);
        pie.setLabelsVisible(true);
        // borderPane.setCenter(pie);
        AnchorPane.setTopAnchor(chart, 0.0);
        AnchorPane.setBottomAnchor(chart, 0.0);
        AnchorPane.setLeftAnchor(chart, 0.0);
        AnchorPane.setRightAnchor(chart, 0.0);
        anchor.getChildren().add(chart);
    }
    @FXML
    private void gender(ActionEvent event){
    anchor.getChildren().clear();

        // borderPane.setCenter(null);
        // borderPane.setTop(null);
        // Label lbl = (Label)borderPane.getTop();
        // lbl.setText("Gender Statistics");

        PieChart pie = (PieChart)chart.getCenter();
        pie.setTitle("Gender");
       UserDAO user = new UserDAO();
        ObservableList<PieChart.Data> pieChartData = user.getUserStatistics("gender");
        pie.setData(pieChartData);
        // pie.setAnimated(true);
        // pie.setClockwise(true); 
        // pie.setLegendVisible(true);
        // pie.setLabelLineLength(50); 
        // pie.setLabelsVisible(true);
        // borderPane.setCenter(pie);
        AnchorPane.setTopAnchor(chart, 0.0);
        AnchorPane.setBottomAnchor(chart, 0.0);
        AnchorPane.setLeftAnchor(chart, 0.0);
        AnchorPane.setRightAnchor(chart, 0.0);
        anchor.getChildren().add(chart);

    }
    @FXML
    private void country(ActionEvent event){
    anchor.getChildren().clear();

        // borderPane.setCenter(null);

        // Label lbl = (Label)borderPane.getTop();
        // lbl.setText("Country Statistics");

        PieChart pie = (PieChart)chart.getCenter();
        pie.setTitle("Country");

        UserDAO user = new UserDAO();
        ObservableList<PieChart.Data> pieChartData = user.getUserStatistics("country");
        pie.setData(pieChartData);
        // pie.setAnimated(true);
        // pie.setLegendVisible(true);
        // pie.setClockwise(true); 
        // pie.setLabelLineLength(50); 
        // pie.setLabelsVisible(true);
        AnchorPane.setTopAnchor(chart, 0.0);
        AnchorPane.setBottomAnchor(chart, 0.0);
        AnchorPane.setLeftAnchor(chart, 0.0);
        AnchorPane.setRightAnchor(chart, 0.0);
        anchor.getChildren().add(chart);

    }



    @FXML
    private void initialize() throws IOException {
        FXMLLoader manageLoader= new FXMLLoader(getClass().getResource("/screens/manage.fxml"));
        manage = manageLoader.load();
        FXMLLoader announceLoader= new FXMLLoader(getClass().getResource("/screens/announce.fxml"));
        announce = announceLoader.load();
        FXMLLoader chartLoader= new FXMLLoader(getClass().getResource("/screens/chart.fxml"));
        chart = chartLoader.load();

        // announce.maxHeightProperty().bind(anchor.heightProperty());
        // announce.maxWidthProperty().bind(anchor.widthProperty());
        
        // anchor.maxHeightProperty().bind(stage.heightProperty().multiply(0.5));
        // anchor.maxWidthProperty().bind(stage.widthProperty().multiply(0.5));
    }
}


