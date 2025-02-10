package gov.iti.jets.mail;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import gov.iti.jets.dto.UserDTO;

public class MailHandler {
    public static void sendMail(UserDTO contactUser) {
        try {
            // Lookup the mail service from the RMI registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1058);
            MailInterface mailService = (MailInterface) registry.lookup("mail");
            
            // Send the email to the contact user
            mailService.sendMail(contactUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}