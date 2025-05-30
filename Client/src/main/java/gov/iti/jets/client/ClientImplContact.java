package gov.iti.jets.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import gov.iti.jets.controller.ChatsController;
import gov.iti.jets.controller.ContactCardController;
import gov.iti.jets.controller.MessageChatController;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import javafx.application.Platform;
import javafx.scene.paint.Color;

public class ClientImplContact extends UnicastRemoteObject implements ClientInt<UserDTO> {
    public int userID;
    public Object msgControl;

    public ClientImplContact(int id, Object m) throws RemoteException {
        super();
        userID = id;
        msgControl = m;

    }

    @Override
    public void sendMessage(UserDTO user) throws RemoteException {
        if (user.getUserID() == userID) {

            if (msgControl instanceof ContactCardController contactCardController) {
                Platform.runLater(() -> {

                    if (user.getUserStatus() == UserStatus.OFFLINE) {
                        contactCardController.getStatus().setFill(Color.GRAY);
                    } else {
                        contactCardController.getStatus().setFill(Color.GREEN);
                    }
                    if (user.getUserPicture() != null) {
                        contactCardController.setPicture(user.getUserPicture());
                    }
                    if (user.getBio() != null) {
                        contactCardController.setBio(user.getBio());
                    }
                    if (user.getName() != null) {
                        contactCardController.setName(user.getName());
                    }
                    if (user.getUserMode() == UserMode.AWAY)
                        contactCardController.getStatus().setFill(Color.YELLOW);
                    else if (user.getUserMode() == UserMode.BUSY)
                        contactCardController.getStatus().setFill(Color.RED);
                    else if (user.getUserMode() == UserMode.AVAILABLE)
                        contactCardController.getStatus().setFill(Color.GREEN);

                });

            }
            if (msgControl instanceof MessageChatController messageChatController) {
                Platform.runLater(() -> {

                    messageChatController.setStatus(user.getUserStatus().toString());

                });

            }
        }
        if (msgControl instanceof ChatsController chatsController) {
            Platform.runLater(() -> {

                chatsController.addOneGroup(user);
            });

        }
    }

    @Override
    public int get() throws RemoteException {
        return userID;
    }

}