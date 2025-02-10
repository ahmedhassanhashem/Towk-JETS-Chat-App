package gov.iti.jets.mail;

import java.rmi.Remote;

import gov.iti.jets.dto.UserDTO;

public interface MailInterface extends Remote {
    public void sendMail(UserDTO user);
}
