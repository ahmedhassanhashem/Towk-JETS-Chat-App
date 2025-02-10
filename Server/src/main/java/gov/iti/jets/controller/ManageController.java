package gov.iti.jets.controller;

import java.io.File;
import java.net.URISyntaxException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.chatbot.ChatbotInterface;
import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.AnnouncementDAOInterface;
import gov.iti.jets.dao.AttachementDAOInterface;
import gov.iti.jets.dao.ChatDAOInterface;
import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.NotificationDAOInterface;
import gov.iti.jets.dao.UserChatDAOInterface;
import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.email.MailInterface;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageController {
    ChatbotInterface chatbot;

    private Stage stage;
    private Scene serverScene;
    private UserDAO userDao;
    private Registry reg;
    UserDAOInterface userDAO;

    AnnouncementDAOInterface announcementDAO;

    AttachementDAOInterface attachementDAO;

    ChatDAOInterface chatDAO;

    MessageDAOInterface messageDAO;

    UserChatDAOInterface userChatDAO;

    ContactDAOInterface contactDAO;

    NotificationDAOInterface notificationDAO;

    MailInterface mail;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private void start(ActionEvent event) {
        RMIConfig p = null;
        try {
            File XMLfile = new File(getClass().getResource("/rmi.xml").toURI());
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(XMLfile);
            // System.out.println(p.getIp() +" " + p.getPort());
        } catch (JAXBException ex) {
            System.out.println(ex.getMessage());
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String ip = p.getIp();
        int port = p.getPort();
        try {
            reg = LocateRegistry.createRegistry(port);
            System.setProperty("sun.rmi.transport.tcp.connectionPool", "true"); 
            System.setProperty("sun.rmi.transport.connectionTimeout", "5000"); 
            System.setProperty("sun.rmi.transport.tcp.maxConnectionThreads", "50"); 
            
            reg.rebind("userDAO", userDAO);

            reg.rebind("announcementDAO", announcementDAO);

            reg.rebind("chatbot", chatbot);

            reg.rebind("attachementDAO", attachementDAO);

            reg.rebind("chatDAO", chatDAO);

            reg.rebind("messageDAO", messageDAO);

            reg.rebind("userChatDAO", userChatDAO);

            reg.rebind("contactDAO", contactDAO);

            reg.rebind("notificationDAO", notificationDAO);

            reg.rebind("mail", mail);

            startButton.setDisable(true);
            stopButton.setDisable(false);
        } catch (RemoteException ex) {
        }
    }

    @FXML
    private void stop(ActionEvent event) {

        try {
            String[] boundNames;

            boundNames = reg.list();
            for (String name : boundNames) {
                reg.unbind(name);
            }
            UnicastRemoteObject.unexportObject(reg, true);
        } catch (AccessException | NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        stopButton.setDisable(true);
        startButton.setDisable(false);

    }

    public void setChatbot(ChatbotInterface chatbot) {
        this.chatbot = chatbot;
    }

    public void setMail(MailInterface mail){
        this.mail = mail;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        // ReadOnlyDoubleProperty h = stage.heightProperty();

        // invalid.minWidthProperty().bind(w);
        // invalid.minHeightProperty().bind(h);
    }

    @FXML
    private void initialize() {

    }

    public void setReg(Registry reg) {
        this.reg = reg;
    }

    public void setUserDAO(UserDAOInterface userDAO) {
        this.userDAO = userDAO;
    }

    public void setAnnouncementDAO(AnnouncementDAOInterface announcementDAO) {
        this.announcementDAO = announcementDAO;
    }

    public void setAttachementDAO(AttachementDAOInterface attachementDAO) {
        this.attachementDAO = attachementDAO;
    }

    public void setChatDAO(ChatDAOInterface chatDAO) {
        this.chatDAO = chatDAO;
    }

    public void setMessageDAO(MessageDAOInterface messageDAO) {
        this.messageDAO = messageDAO;
    }

    public void setUserChatDAO(UserChatDAOInterface userChatDAO) {
        this.userChatDAO = userChatDAO;
    }

    public void setContactDAO(ContactDAOInterface contactDAO) {
        this.contactDAO = contactDAO;
    }

    public void setnotificationDAO(NotificationDAOInterface notificationDAO) {
        this.notificationDAO = notificationDAO;
    }
}
