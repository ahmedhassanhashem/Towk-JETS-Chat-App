package gov.iti.jets.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.controller.MessageChatController;
import gov.iti.jets.dto.MessageDTO;
import javafx.application.Platform;

public class ClientImpl extends UnicastRemoteObject implements ClientInt<MessageDTO> {
    public int chatID ;
    public int userID;
    public MessageChatController msgControl;
    public ClientImpl(int id,int userID,MessageChatController m) throws RemoteException {
        super();
        chatID = id;
        msgControl = m;
        this.userID = userID;
        
    }

    @Override
    public void sendMessage(MessageDTO message) throws RemoteException {
        // System.out.println("Received message: " + message);
        // App.controller.setSc(message);
                    Platform.runLater(()->{

        msgControl.addToChats(message);
    });
}

    @Override
    public int get() throws RemoteException{
        return userID;
    }

}