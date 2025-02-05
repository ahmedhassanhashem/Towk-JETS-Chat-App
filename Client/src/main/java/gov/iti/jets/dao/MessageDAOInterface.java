package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.MessageDTO;

public interface MessageDAOInterface extends Remote{


    public MessageDTO create(MessageDTO msg) throws RemoteException ;

    public List<MessageDTO> findAllMessages(int chatID) throws RemoteException ;

    public String findLastMessageGroup(int chatID) throws RemoteException ;

    public MessageDTO findLastMessage(int chatID) throws RemoteException;

    public String findLastMessage(int user1, int user2) throws RemoteException ;

        public void register(int chatID,ClientInt clientRef) throws RemoteException;

    public void unRegister(int chatID,ClientInt clientRef) throws RemoteException ;

}
