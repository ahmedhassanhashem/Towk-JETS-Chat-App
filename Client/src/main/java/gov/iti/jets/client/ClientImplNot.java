package gov.iti.jets.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.controller.NotificationController;
import gov.iti.jets.dto.NotificationDTO;
import gov.iti.jets.dto.UserDTO;
import javafx.application.Platform;

public class ClientImplNot extends UnicastRemoteObject implements ClientInt<Object> {
    public int userID;
    public NotificationController msgControl;
    public ClientImplNot(int userID,NotificationController m) throws RemoteException {
        super();

        msgControl = m;
        this.userID = userID;
        
    }

    @Override
    public void sendMessage(Object message) throws RemoteException {

        if(message instanceof NotificationDTO notificationDTO){

            Platform.runLater(()->{
                
                msgControl.addNot(notificationDTO);
            });
        }
        if(message instanceof UserDTO userDTO){
            Platform.runLater(()->{
                
                msgControl.addUser(userDTO);
            });
        }
        
}

    @Override
    public int get() throws RemoteException{
        return userID;
    }

}