package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import gov.iti.jets.dto.UserDTO;

public interface ContactDAOInterface extends Remote{

    public String create(String senderPhone, String receiverPhone) throws RemoteException ;

    public boolean checkSent(String senderPhone, String receiverPhone) throws RemoteException ;


    // to get all the contacts to show in the contacts list
    public List<UserDTO> findAllContactsACCEPTED(String userPhone) throws RemoteException ;

    // to get all the rejected to show in the notfication that you are rejected
    public List<UserDTO> findAllContactsREJECTED(String userPhone) throws RemoteException ;

    // to get all the pending request so you can list in the notfication to accept or reject
    public List<UserDTO> findAllContactsPENDING(String userPhone) throws RemoteException ;

}

