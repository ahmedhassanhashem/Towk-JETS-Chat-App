package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import gov.iti.jets.dto.UserDTO;

public interface ChatDAOInterface extends Remote {

    public Integer createSingle(String currentUserPhone, String otherUserPhone) throws RemoteException;

    public String createGroup(String creatorPhone, List<String> participantPhones, String groupName) throws RemoteException;

    public Integer findExistingSingleChat(int user1, int user2) throws SQLException,RemoteException;

    public int getUserIdByPhone(String phone) throws SQLException,RemoteException;

    public List<UserDTO> findAllGroups(int userId) throws RemoteException;

    public int updateChatPicture(int chatId, String fileName, byte[] chatPicture) throws RemoteException ;

}
