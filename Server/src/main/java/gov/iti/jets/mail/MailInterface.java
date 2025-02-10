package gov.iti.jets.mail;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gov.iti.jets.dto.UserDTO;

public interface MailInterface extends Remote {
    public void sendMail(UserDTO user) throws RemoteException;
}
