package gov.iti.jets.chatbot;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
public class ChatbotImpl extends UnicastRemoteObject implements ChatbotInterface{
    public ChatbotImpl() throws RemoteException {
        super();
    }
    @Override
    public String sendMessage(String message) throws RemoteException {
        return GeminiAPI.getBotResponse(message);
    }
}