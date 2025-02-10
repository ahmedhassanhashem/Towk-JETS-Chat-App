package gov.iti.jets.mail;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.dto.UserDTO;

public class MailImpl extends UnicastRemoteObject implements MailInterface{

    public MailImpl() throws RemoteException {
        super();
    }

    @Override
    public void sendMail(UserDTO user) {
        JakartaMail.mailService(user);
    }

    
}
