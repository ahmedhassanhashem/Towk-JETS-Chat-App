package gov.iti.jets.dao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.server.Images;

public class ChatDAO extends UnicastRemoteObject implements ChatDAOInterface{

    DatabaseConnectionManager dm;
    UserChatDAO userChatDAO = new UserChatDAO();
    MessageDAO messageDAO = new MessageDAO();
    Images images = new Images();

    public ChatDAO() throws RemoteException{
        super();
        dm = DatabaseConnectionManager.getInstance();
    }


    public Integer createSingle(String currentUserPhone, String otherUserPhone) throws RemoteException {
        Connection con = dm.getConnection();
        try {
            
            if (currentUserPhone == null || otherUserPhone == null 
                || currentUserPhone.length() != 11 || otherUserPhone.length() != 11) {
                return 0;
            }

            // Get user IDs
            int currentUserId = getUserIdByPhone(currentUserPhone);
            int otherUserId = getUserIdByPhone(otherUserPhone);
            
            if (currentUserId == -1 || otherUserId == -1) {
                return 0;
            }

            // Check for existing chat
            Integer existingChatId = findExistingSingleChat(currentUserId, otherUserId);
            if (existingChatId != null) {
                return 0;
            }

            // Create new chat
            con.setAutoCommit(false);
            int chatId = createNewChat("SINGLE", null);
            linkUsersToChat(chatId, Arrays.asList(currentUserId, otherUserId));
            con.commit();

            return chatId;
            
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return 0;
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    

 
    public String createGroup(String creatorPhone, List<String> participantPhones, String groupName) throws RemoteException {
    try (Connection con = dm.getConnection();){
        List<Integer> userIds = new ArrayList<>();
        
        // Get creator ID
        int creatorId = getUserIdByPhone(creatorPhone);
        if (creatorId == -1) return "Creator not found";
        userIds.add(creatorId);
        
        // Get participant IDs
        for (String phone : participantPhones) {
            int userId = getUserIdByPhone(phone);
            if (userId != -1) userIds.add(userId);
        }
        
        if (userIds.size() < 2) return "Need at least 2 participants";
        
        con.setAutoCommit(false);
        int chatId = createNewChat("GROUP", groupName);
        linkUsersToChat(chatId, userIds);
        con.commit();
        
        return "Group chat created with ID: " + chatId;
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
        return groupName;
}

    public Integer findExistingSingleChat(int user1, int user2) throws SQLException,RemoteException {
        String query ="SELECT c.chatID \r\n" + //
                        "            FROM Chat c\r\n" + //
                        "            JOIN UserChat uc1 ON c.chatID = uc1.chatID\r\n" + //
                        "            JOIN UserChat uc2 ON c.chatID = uc2.chatID\r\n" + //
                        "            WHERE c.chatType = 'SINGLE'\r\n" + //
                        "              AND uc1.userID = ?\r\n" + //
                        "              AND uc2.userID = ?";
        
        try (Connection con = dm.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, user1);
            ps.setInt(2, user2);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("chatID") : 0;
        }
    }

    private int createNewChat(String chatType, String chatName) throws SQLException {
        String query = "INSERT INTO Chat (chatType, chatName) VALUES (?, ?)";
        try (Connection con = dm.getConnection();
            PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, chatType);
            ps.setString(2, chatName);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("Failed to get generated chat ID");
        }
    }

    private void linkUsersToChat(int chatId, List<Integer> userIds) throws SQLException {
        String query = "INSERT INTO UserChat (chatID, userID) VALUES (?, ?)";
        try (Connection con = dm.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            for (int userId : userIds) {
                ps.setInt(1, chatId);
                ps.setInt(2, userId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }



    public int getUserIdByPhone(String phone) throws SQLException,RemoteException {
        String query = "SELECT userID FROM User WHERE phone = ?";
        try (Connection con = dm.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("userID") : -1;
        }
    }

        public List<UserDTO> findAllGroups(int userId) throws RemoteException {
        // ObservableList<UserDTO> allGroups = FXCollections.observableArrayList();
 List<UserDTO> allGroups = new ArrayList<>();
        String query = "select c.chatID , chatName,chatPicture from Chat c join UserChat u on c.chatID = u.chatID where userID = ? and chatType = \"GROUP\"";
        try (Connection con = dm.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (true) {
                // fix bug
                // reusing the same UserDTO instance for each group in the while loop, 
                //leading to overwriting of group names. 
                //Creating a new instance each time is necessary.
                UserDTO group = new UserDTO();
                if(!rs.next())break;
                UserDTO group = new UserDTO();
                group.setUserID(rs.getInt("chatID"));
                group.setName(rs.getString("chatName"));
                // group.setUserPicture(rs.getString("chatPicture"));
                if(rs.getString("chatPicture") != null && rs.getString("chatPicture").length()>0) {
                    group.setUserPicture(images.downloadPP(rs.getString("chatPicture")));
                } else  {
                    group.setUserPicture(null);
                }
                // user = convert(rs);
                // if(user == null)break;
                allGroups.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allGroups;
        // TODO Auto-generated method stub
        
    }
    
    public int updateChatPicture(int chatId, String fileName, byte[] chatPicture) throws RemoteException {
        String query = "UPDATE Chat " +
                       "SET chatPicture = ? " +
                       "WHERE chatID = ?";

        try (Connection con = dm.getConnection();
             PreparedStatement stmnt = con.prepareStatement(query)) {

            if (chatPicture != null) {
                images.uploadPP(fileName, chatPicture); 
                stmnt.setString(1, fileName);
            } else {
                stmnt.setNull(1, Types.CHAR); 
            }

            stmnt.setInt(2, chatId);

            int rowsUpdated = stmnt.executeUpdate();
            return rowsUpdated; 

        } catch (SQLException e) {
            e.printStackTrace(); 
            return 0; 
        }
    }

}
