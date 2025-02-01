package gov.iti.jets.controller;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import gov.iti.jets.dao.AnnouncementDAO;
import gov.iti.jets.dao.AnnouncementDAOInterface;
import gov.iti.jets.dao.AttachementDAO;
import gov.iti.jets.dao.AttachementDAOInterface;
import gov.iti.jets.dao.ChatDAO;
import gov.iti.jets.dao.ChatDAOInterface;
import gov.iti.jets.dao.ContactDAO;
import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dao.MessageDAO;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.UserChatDAO;
import gov.iti.jets.dao.UserChatDAOInterface;
import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.server.FileServer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServerController {

    private Stage stage;
    private BorderPane manage;
    private BorderPane announce;
    private BorderPane chart;
    private Scene login;
    private UserDAO userDao;
    // private Scene signup;

    @FXML
    private AnchorPane anchor;
    @FXML
    private BorderPane borderPane;

    @FXML
    private void signOut(ActionEvent event) {
        stage.setScene(login);
    }
    // @FXML
    // private void gotoSingup(){
    //     stage.setScene(signup);
    // }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest((e)->{
            FileServer.running = false;
        });

    }

    public void setLogin(Scene login) {
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
    private void manageButton(ActionEvent event) {
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
    private void announceButton(ActionEvent event) {
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
    private void status(ActionEvent event) {
        anchor.getChildren().clear();

        // borderPane.setCenter(null);
        // Label lbl = (Label)borderPane.getTop();
        // lbl.setText("Status Statistics");
        PieChart pie = (PieChart) chart.getCenter();
        pie.setTitle("Status");
        // UserDAO user = new UserDAO();
        ObservableList<PieChart.Data> pieChartData = null;
        try {
            pieChartData = userDao.getUserStatistics("userStatus");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
    private void gender(ActionEvent event) {
        anchor.getChildren().clear();

        // borderPane.setCenter(null);
        // borderPane.setTop(null);
        // Label lbl = (Label)borderPane.getTop();
        // lbl.setText("Gender Statistics");
        PieChart pie = (PieChart) chart.getCenter();
        pie.setTitle("Gender");
        //    UserDAO user = new UserDAO();
        ObservableList<PieChart.Data> pieChartData = null;
        try {
            pieChartData = userDao.getUserStatistics("gender");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
    private void country(ActionEvent event) {
        anchor.getChildren().clear();

        // borderPane.setCenter(null);
        // Label lbl = (Label)borderPane.getTop();
        // lbl.setText("Country Statistics");
        PieChart pie = (PieChart) chart.getCenter();
        pie.setTitle("Country");

        // UserDAO user = new UserDAO();
        ObservableList<PieChart.Data> pieChartData = null;
        try {
            pieChartData = userDao.getUserStatistics("country");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
                Properties props = new Properties();
        
        try (InputStream input = getClass().getResourceAsStream("/rmi.properties")) {
            if (input == null) {
                throw new IOException("Properties file not found");
            }
            // System.out.println(new String(input.readAllBytes()));
            props.load(input);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        // System.out.println(props.getProperty("rmi_port"));
        String ip = props.getProperty("rmi_ip");
        int port = Integer.parseInt(props.getProperty("rmi_port"));
        Registry reg = LocateRegistry.createRegistry(port);

        UserDAOInterface userDAO = new UserDAO();
        reg.rebind("userDAO", userDAO);

        AnnouncementDAOInterface announcementDAO = new AnnouncementDAO();
        reg.rebind("announcementDAO", announcementDAO);

        AttachementDAOInterface attachementDAO = new AttachementDAO();
        reg.rebind("attachementDAO", attachementDAO);

        ChatDAOInterface chatDAO = new ChatDAO();
        reg.rebind("chatDAO", chatDAO);

        MessageDAOInterface messageDAO = new MessageDAO();
        reg.rebind("messageDAO", messageDAO);

        UserChatDAOInterface userChatDAO = new UserChatDAO();
        reg.rebind("userChatDAO", userChatDAO);

        ContactDAOInterface contactDAO = new ContactDAO();
        reg.rebind("contactDAO", contactDAO);

    Thread fileserver = new Thread(() -> FileServer.Start());
        fileserver.setDaemon(true);
        fileserver.start();



        try {
            userDao = new UserDAO();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FXMLLoader manageLoader = new FXMLLoader(getClass().getResource("/screens/manage.fxml"));
        manage = manageLoader.load();
        ManageController manageController = manageLoader.getController();
        manageController.setReg(reg);
        manageController.setAnnouncementDAO(announcementDAO);
        manageController.setAttachementDAO(attachementDAO);
        manageController.setChatDAO(chatDAO);
        manageController.setContactDAO(contactDAO);
        manageController.setMessageDAO(messageDAO);
        manageController.setUserChatDAO(userChatDAO);
        manageController.setUserDAO(userDAO);
        FXMLLoader announceLoader = new FXMLLoader(getClass().getResource("/screens/announce.fxml"));
        announce = announceLoader.load();
        FXMLLoader chartLoader = new FXMLLoader(getClass().getResource("/screens/chart.fxml"));
        chart = chartLoader.load();

        // announce.maxHeightProperty().bind(anchor.heightProperty());
        // announce.maxWidthProperty().bind(anchor.widthProperty());
        // anchor.maxHeightProperty().bind(stage.heightProperty().multiply(0.5));
        // anchor.maxWidthProperty().bind(stage.widthProperty().multiply(0.5));
    }
}
