package gov.iti.jets.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.controller.ChatCadController;
import gov.iti.jets.controller.ContactCardController;
import gov.iti.jets.controller.MessageChatController;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserStatus;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public class ClientImplChat extends UnicastRemoteObject implements ClientInt<MessageDTO> {

    public Object msgControl;
    public int chatID;

    public ClientImplChat(int chatID, Object m) throws RemoteException {
        super();
        this.chatID = chatID;
        msgControl = m;

    }

    @Override
    public void sendMessage(MessageDTO user) throws RemoteException {

        if (user.getChatID() == chatID) {

            if (msgControl instanceof ChatCadController chatCadController) {
                String ret = user.getMessageContent();
                if (ret.length() > 7)
                    ret = ret.substring(0, 7) + "...";
                String ret2= ret;
                {
                    Platform.runLater(() -> {
                        chatCadController.setText(ret2);
                    });

                }

            }
            
        }
    }
    

}