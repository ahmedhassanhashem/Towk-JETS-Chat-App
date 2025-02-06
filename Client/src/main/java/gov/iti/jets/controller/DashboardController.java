package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.mysql.cj.xdevapi.ClientImpl;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserStatus;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DashboardController {

    private Stage stage;
    private Scene dashScene;
    private UserDTO userDTO = new UserDTO();
    private ScheduledExecutorService scheduledExecutorService;
    UserDAOInterface userDAO;
    ChatsController chat;
    private DashboardController dashboardController;
    @FXML
    private Label nameLabel;

    @FXML
    private ImageView profileImage;

    @FXML
    private BorderPane borderPane;

    public void setUserDTO(UserDTO user) {
        userDTO = user;
        String fullName = user.getName();
        String firstName = extractFirstName(fullName);
        nameLabel.setText(firstName);
        if (user.getUserPicture() != null) {
            // System.out.println(user.getUserPicture().length);
            ByteArrayInputStream bis = new ByteArrayInputStream(user.getUserPicture());
            Image image = new Image(bis);
            profileImage.setImage(image);
        }
        chat.setUserDTO(userDTO);
        chat.chatScene();
    }

    public void changepp() {
        nameLabel.setText(userDTO.getName());
        if (userDTO.getUserPicture() != null) {
            // System.out.println(user.getUserPicture().length);
            ByteArrayInputStream bis = new ByteArrayInputStream(userDTO.getUserPicture());
            Image image = new Image(bis);
            profileImage.setImage(image);
        }
    }

    public void setDashScene(Scene l) {
        dashScene = l;
    }

    public void setStage(Stage s) {
        stage = s;
        scheduledExecutorService = Executors.newScheduledThreadPool(20);
        chat.setScheduledExecutorService(scheduledExecutorService);
        chat.setStage(s);
        // System.out.println(stage);
        Platform.runLater(() -> {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {

                try {
                        userDAO.changeStatus(userDTO.getUserID(), UserStatus.OFFLINE.toString());
                        userDTO.setUserStatus(UserStatus.OFFLINE);
                        userDAO.propagateOffline(userDTO);
                    
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                

        }));
        });
        try {
            RMIConfig p = null;

            File XMLfile = new File(getClass().getResource("/rmi.xml").toURI());
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(XMLfile);
            // System.out.println(p.getIp() +" " + p.getPort());

            String ip = p.getIp();
            int port = p.getPort();

            Registry reg;
            reg = LocateRegistry.getRegistry(ip, port);
            userDAO = (UserDAOInterface) reg.lookup("userDAO");
            
            
          

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        c.setStage(stage);
        c.contactScene(hold);
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
        c.setStage(stage);
        c.chatScene();
        c.setScheduledExecutorService(scheduledExecutorService);
        borderPane.setCenter(hold);

    }

    @FXML
    private void userInfo(MouseEvent event) {
        Stage info = new Stage();
        info.initOwner(stage);
        info.initStyle(StageStyle.UNDECORATED);
        info.initStyle(StageStyle.TRANSPARENT);
        info.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader userInfoLoader = new FXMLLoader(getClass().getResource("/screens/User_Info.fxml"));
        BorderPane userInfo;
        try {
            userInfo = userInfoLoader.load();
            UserInfoController userInfoController = userInfoLoader.getController();
            var userInfoScene = new Scene(userInfo);
            userInfoController.makeWindowDraggable(info);
            Platform.runLater(() -> userInfoController.applyRoundedCorners(userInfo, 15));
            userInfoScene.setFill(Color.TRANSPARENT);
            info.setScene(userInfoScene);
            userInfoController.setUserDTO(userDTO);
            userInfoController.loadUserData();
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
            settingsController.setDashboardController(dashboardController);
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
        c.setStage(stage);
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
        NotificationController c = chatLoader.getController();
        System.out.println("USERDTO" + userDTO.getPhone());
        c.setUserDTO(userDTO);
        c.loadNotifications();
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
        String fullName = userDTO.getName();
        String firstName = extractFirstName(fullName);
        dashController.setNameField(firstName + "!");
        if (userDTO.getUserPicture() != null)
            dashController.setProfileImage(userDTO.getUserPicture());
        userDTO.setPassword(null);
        dashController.setUdto(userDTO);

        stage.setScene(dashScene);
        try {
            RMIConfig p = null;

            File XMLfile = new File(getClass().getResource("/rmi.xml").toURI());
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(XMLfile);
            // System.out.println(p.getIp() +" " + p.getPort());

            String ip = p.getIp();
            int port = p.getPort();

            Registry reg;
            reg = LocateRegistry.getRegistry(ip, port);
            UserDAOInterface userDAO = (UserDAOInterface) reg.lookup("userDAO");

            userDAO.changeStatus(userDTO.getUserID(), UserStatus.OFFLINE.toString());
            userDTO.setUserStatus(UserStatus.OFFLINE);
            userDAO.propagateOffline(userDTO);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

        Circle clip = new Circle();
        clip.setRadius(30);
        clip.setCenterX(30);
        clip.setCenterY(30);
        profileImage.setClip(clip);

    }

    private String extractFirstName(String fullName) {
        String[] names = fullName.split("\\s+");

        if (names.length > 0) {
            return names[0].trim();
        } else {
            return "";
        }
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
}
