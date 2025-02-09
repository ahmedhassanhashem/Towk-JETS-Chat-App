package gov.iti.jets.client;

import java.io.File;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.jsoup.Jsoup;

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.controller.NotificationController;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.NotificationDTO;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import java.awt.*;
public class ClientImplNot extends UnicastRemoteObject implements ClientInt<Object> {
    public int userID;
    public NotificationController msgControl;
    private MessageDAOInterface messageDAO;    private UserDAOInterface userDAO;

    Registry reg;
    public ClientImplNot(int userID,NotificationController m) throws RemoteException {
        super();
 RMIConfig p = null;

 try {
            File XMLfile = new File(getClass().getResource("/rmi.xml").toURI());
            JAXBContext context;
                context = JAXBContext.newInstance(RMIConfig.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                p = (RMIConfig) unmarshaller.unmarshal(XMLfile);
                // System.out.println(p.getIp() +" " + p.getPort());
                
                String ip = p.getIp();
                int port = p.getPort();
    
                reg = LocateRegistry.getRegistry(ip, port);
                            messageDAO = (MessageDAOInterface) reg.lookup("messageDAO");
            userDAO = (UserDAOInterface) reg.lookup("userDAO");
        } catch (JAXBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NotBoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        msgControl = m;
        this.userID = userID;
        
    }

    @Override
    public void sendMessage(Object message) throws RemoteException {

        // System.out.println("received ");
        if(message instanceof NotificationDTO notificationDTO){
            if(msgControl ==null){
                Platform.runLater(()->{


                    try {
                    MessageDTO chat = messageDAO.read(notificationDTO.getMessageID());
                    

                    SystemTray tray = SystemTray.getSystemTray();
                    TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(""), "Notification");
                    trayIcon.setImageAutoSize(true);
                        tray.add(trayIcon);
                        trayIcon.displayMessage("New Message from " + userDAO.read(chat.getUserID())
                        ,Jsoup.parse(chat.getMessageContent()).text(), TrayIcon.MessageType.INFO);
                    } catch (AWTException | RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                
                });
            
            }else{

                Platform.runLater(()->{
                    
                    msgControl.addNot(notificationDTO);
                });
            }
        }
        else if(message instanceof UserDTO userDTO){
            if(msgControl ==null){
//                 Notifications notification = Notifications.NOTICE;
                System.out.println("asdaaa");

// TrayNotification tray = new TrayNotification("New Friend request !", userDTO.getName() + " Wants to be friend", notification);
// tray.showAndWait();
try {


    SystemTray tray = SystemTray.getSystemTray();
    TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(""), "Notification");
    trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
        trayIcon.displayMessage("New Friend request !"
        ,userDTO.getName() + " Wants to be friend", TrayIcon.MessageType.INFO);
    } catch (AWTException  e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
            
            }{

                Platform.runLater(()->{
                    
                    msgControl.addUser(userDTO);
                });
            }
        }
        
}

    @Override
    public int get() throws RemoteException{
        return userID;
    }

}