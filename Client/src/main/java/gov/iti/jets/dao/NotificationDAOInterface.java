package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.AnnouncementDTO;
import gov.iti.jets.dto.NotificationDTO;

public interface NotificationDAOInterface extends Remote {

public NotificationDTO create(NotificationDTO msg) throws RemoteException ;
public void delete(int userID,int messageID) throws RemoteException ;
public List<NotificationDTO> getNotifications(int userID) throws RemoteException;
public void register(int userID,ClientInt clientRef) throws RemoteException;
public void unRegister(int userID,ClientInt clientRef) throws RemoteException;
public boolean isSeen(int msgID) throws RemoteException;
}
