package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gov.iti.jets.dto.AttachementDTO;

public interface AttachementDAOInterface extends Remote {


    public String getAttachmentTitle(int id) throws RemoteException ;

    public int getAttachmentID(String title) throws RemoteException ;
    
    public AttachementDTO createAttachment(AttachementDTO attachementDTO) throws RemoteException ;
}
