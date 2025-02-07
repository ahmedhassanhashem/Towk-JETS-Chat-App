package gov.iti.jets.chatbot;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface ChatbotInterface extends Remote{
        String sendMessage(String message) throws RemoteException;
}