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

public class ClientImplChat extends UnicastRemoteObject implements ClientInt<Object> {

    public Object msgControl;
    public int chatID;

    public ClientImplChat(int chatID, Object m) throws RemoteException {
        super();
        this.chatID = chatID;
        System.out.println(chatID);
        msgControl = m;

    }

    @Override
    public void sendMessage(Object user) throws RemoteException {
        if(user instanceof MessageDTO messageDTO){

            if (messageDTO.getChatID() == chatID) {
                
                if (msgControl instanceof ChatCadController chatCadController) {
                    String ret = messageDTO.getMessageContent();
                    // if (ret.length() > 7)
                    //     ret = ret.substring(0, 7) + "...";
                    String ret2= ret;
                    {
                        Platform.runLater(() -> {
                            chatCadController.setText(ret2);
                        });
                        
                    }
                    
                }
                
            }
        }else if(user instanceof UserDTO userDTO){
            if (userDTO.getUserID() == chatID) {

            if(msgControl instanceof ChatCadController chatCadController){
                Platform.runLater(() -> {

                if (userDTO.getUserPicture() != null) {
                    chatCadController.setImage(userDTO.getUserPicture());
                }
                if (userDTO.getName() != null) {
                    chatCadController.setLabel(userDTO.getName());
                }
            });

            }
        }
    }
    }

    @Override
    public int get() throws RemoteException{
        return chatID;
    }
    

}