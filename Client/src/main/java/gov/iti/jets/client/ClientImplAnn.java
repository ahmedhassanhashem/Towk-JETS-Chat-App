package gov.iti.jets.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.controller.AnnouncementController;
import gov.iti.jets.controller.NotificationController;
import gov.iti.jets.dto.AnnouncementDTO;
import gov.iti.jets.dto.NotificationDTO;
import gov.iti.jets.dto.UserDTO;
import javafx.application.Platform;

public class ClientImplAnn extends UnicastRemoteObject implements ClientInt<AnnouncementDTO> {
    public int userID;
    public AnnouncementController msgControl;
    public ClientImplAnn(int userID,AnnouncementController m) throws RemoteException {
        super();

        msgControl = m;
        this.userID = userID;
        
    }

    @Override
    public void sendMessage(AnnouncementDTO message) throws RemoteException {
    msgControl.addOne(message);
        
}

    @Override
    public int get() throws RemoteException{
        return userID;
    }

}