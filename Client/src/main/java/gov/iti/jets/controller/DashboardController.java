package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.mysql.cj.xdevapi.ClientImpl;

import gov.iti.jets.client.ClientImplAnn;
import gov.iti.jets.client.ClientImplChat;
import gov.iti.jets.client.ClientImplContact;
import gov.iti.jets.client.ClientImplNot;
import gov.iti.jets.chatbot.BotService;
import gov.iti.jets.chatbot.BotService;
import gov.iti.jets.client.ClientImplChat;
import gov.iti.jets.client.ClientImplContact;
import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.AnnouncementDAOInterface;
import gov.iti.jets.dao.ChatDAOInterface;
import gov.iti.jets.dao.NotificationDAOInterface;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DashboardController {
    private boolean botService = false;
    private Stage stage;
    private Scene dashScene;
    private UserDTO userDTO = new UserDTO();
    private ScheduledExecutorService scheduledExecutorService;
    BorderPane hold2;
    UserDAOInterface userDAO;
    ChatDAOInterface chatDAO;
        private AnnouncementDAOInterface announcementDAO;

    private NotificationDAOInterface notificationDAO;
    ChatsController chat;
    ClientImplContact clientImplContact;
    ClientImplNot clientImplNot ;
    ClientImplAnn clientImplAnn;
    private DashboardController dashboardController;

    @FXML
    private HBox bot;

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
        chat.chatScene(hold2);
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
            chatDAO = (ChatDAOInterface) reg.lookup("chatDAO");
            notificationDAO = (NotificationDAOInterface) reg.lookup("notificationDAO");
            announcementDAO = (AnnouncementDAOInterface) reg.lookup("announcementDAO");

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
        c.chatScene(hold);
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
        c.groupScene(hold);
        c.setScheduledExecutorService(scheduledExecutorService);
        borderPane.setCenter(hold);

        if (clientImplContact != null) {
            // unloadChat(entry.getKey(), entry.getValue());
            try {
                chatDAO.unRegister(userDTO.getUserID(), clientImplContact);
                UnicastRemoteObject.unexportObject(clientImplContact, true);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
            }

        }
        try {
            clientImplContact = new ClientImplContact(0, c);

            chatDAO.register(userDTO.getUserID(), clientImplContact);

            hold.sceneProperty().addListener((observable, oldScene, newScene) -> {
                // if (oldScene != null && newScene!= null) {
                if (clientImplContact != null) {
                    // unloadChat(entry.getKey(), entry.getValue());
                    try {
                        chatDAO.unRegister(userDTO.getUserID(), clientImplContact);
                        // UnicastRemoteObject.unexportObject(clientImplContact, true);
                        boolean unexported = UnicastRemoteObject.unexportObject(clientImplContact, true);
                        // System.out.println("Unexport result: " + unexported);
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }

                // }
            });

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Platform.runLater(() -> {

            stage.setOnCloseRequest((e) -> {

                settings(event);
                StackPane temp = new StackPane();
                StackPane temp2 = new StackPane();
                borderPane.setCenter(temp);
                stage.setScene(new Scene(temp2));
                try {
                    chatDAO.unRegister(userDTO.getUserID(), clientImplContact);
                    // UnicastRemoteObject.unexportObject(clientImplContact, true);
                    UnicastRemoteObject.unexportObject(clientImplContact, true);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }
                new Thread(()->{
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }).start();;
                System.exit(0);
                Platform.exit();
            });

        });

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

        if (clientImplNot != null) {
            // unloadChat(entry.getKey(), entry.getValue());
            try {
                notificationDAO.unRegister(userDTO.getUserID(), clientImplNot);
                UnicastRemoteObject.unexportObject(clientImplNot, true);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
            }

        }
        try {
            clientImplNot = new ClientImplNot(userDTO.getUserID(), c);
            notificationDAO.register(userDTO.getUserID(), clientImplNot);


            
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        hold.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null ) {
            if (clientImplNot != null) {
                // unloadChat(entry.getKey(), entry.getValue());
                try {
                    notificationDAO.unRegister(userDTO.getUserID(), clientImplNot);
                    UnicastRemoteObject.unexportObject(clientImplNot, true);
                    // System.out.println("Unexport result: " + unexported);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

            }
        });
        Platform.runLater(() -> {

            stage.setOnCloseRequest((e) -> {

                // settings(event);
                // StackPane temp = new StackPane();
                // StackPane temp2 = new StackPane();
                // borderPane.setCenter(temp);
                // stage.setScene(new Scene(temp2));
                try {
                    notificationDAO.unRegister(userDTO.getUserID(), clientImplNot);
                    UnicastRemoteObject.unexportObject(clientImplNot, true);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }
                System.exit(0);
                Platform.exit();
            });

        });
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
        AnnouncementController c = chatLoader.getController();

        if (clientImplAnn != null) {
            // unloadChat(entry.getKey(), entry.getValue());
            try {
                announcementDAO.unRegister(userDTO.getUserID(), clientImplAnn);
                UnicastRemoteObject.unexportObject(clientImplAnn, true);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                // e1.printStackTrace();
            }

        }
        try {
            clientImplAnn = new ClientImplAnn(userDTO.getUserID(), c);
            announcementDAO.register(userDTO.getUserID(), clientImplAnn);


            
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        hold.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null ) {
            if (clientImplAnn != null) {
                // unloadChat(entry.getKey(), entry.getValue());
                try {
                    announcementDAO.unRegister(userDTO.getUserID(), clientImplAnn);
                    UnicastRemoteObject.unexportObject(clientImplAnn, true);
                    // System.out.println("Unexport result: " + unexported);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

            }
        });
        Platform.runLater(() -> {

            stage.setOnCloseRequest((e) -> {

                // settings(event);
                // StackPane temp = new StackPane();
                // StackPane temp2 = new StackPane();
                // borderPane.setCenter(temp);
                // stage.setScene(new Scene(temp2));
                try {
                    announcementDAO.unRegister(userDTO.getUserID(), clientImplAnn);
                    UnicastRemoteObject.unexportObject(clientImplAnn, true);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }
                System.exit(0);
                Platform.exit();
            });

        });

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

            userDAO.changeStatus(userDTO.getUserID(), UserStatus.OFFLINE.toString());
            userDTO.setUserStatus(UserStatus.OFFLINE);
            userDAO.propagateOffline(userDTO);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    private void chatbot(){
        botService = !botService;
        BotService.getInstance().setBotServiceStatus(botService);
        if(botService){
            bot.getStyleClass().remove("hbox");
            bot.getStyleClass().add("selected");
        }else{
            bot.getStyleClass().remove("selected");
            bot.getStyleClass().add("hbox");
        }
    }
   

    @FXML
    private void initialize() {

        hold2 = null;
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/base1.fxml"));

        try {
            hold2 = chatLoader.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        borderPane.setCenter(hold2);
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
