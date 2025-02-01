package gov.iti.jets.controller;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import gov.iti.jets.dao.AnnouncementDAOInterface;
import gov.iti.jets.dao.AttachementDAOInterface;
import gov.iti.jets.dao.ChatDAOInterface;
import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.UserChatDAOInterface;
import gov.iti.jets.dao.UserDAO;
import gov.iti.jets.dao.UserDAOInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageController {

    private Stage stage;
    private Scene serverScene;
    private UserDAO userDao;
    private Registry reg;
     UserDAOInterface userDAO ;

        AnnouncementDAOInterface announcementDAO ;


        AttachementDAOInterface attachementDAO;


        ChatDAOInterface chatDAO ;

        MessageDAOInterface messageDAO ;

        UserChatDAOInterface userChatDAO ;

        ContactDAOInterface contactDAO ;
    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private void start(ActionEvent event) {
        try {
            reg.rebind("userDAO", userDAO);
            
            
            reg.rebind("announcementDAO", announcementDAO);
            
            
            reg.rebind("attachementDAO", attachementDAO);
            
            
            reg.rebind("chatDAO", chatDAO);
            
            
            reg.rebind("messageDAO", messageDAO);
            
            
            reg.rebind("userChatDAO", userChatDAO);
            
            
            reg.rebind("contactDAO", contactDAO);
            startButton.setDisable(true);
            stopButton.setDisable(false);
        } catch (RemoteException ex) {
        }
    }

    @FXML
    private void stop(ActionEvent event) {

        try {
            reg.unbind("userDAO");
            
            
            reg.unbind("announcementDAO");
            
            
            reg.unbind("attachementDAO");
            
            
            reg.unbind("chatDAO");
            
            
            reg.unbind("messageDAO");
            
            
            reg.unbind("userChatDAO");
            
            
            reg.rebind("contactDAO", contactDAO);
            stopButton.setDisable(true);
            startButton.setDisable(false);
        } catch (RemoteException ex) {
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
}
