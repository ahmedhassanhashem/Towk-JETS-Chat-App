package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.AnnouncementDTO;

public interface AnnouncementDAOInterface extends Remote {

    public AnnouncementDTO create(AnnouncementDTO announcement) throws RemoteException ;


    public AnnouncementDTO read(AnnouncementDTO announcement) throws RemoteException;

    public AnnouncementDTO update(AnnouncementDTO announcement) throws RemoteException;


    public void delete(AnnouncementDTO announcement)throws RemoteException ;

    
    public List<AnnouncementDTO> findAll() throws RemoteException ;

    public void register(int userID,ClientInt clientRef) throws RemoteException;
public void unRegister(int userID,ClientInt clientRef) throws RemoteException;

}
