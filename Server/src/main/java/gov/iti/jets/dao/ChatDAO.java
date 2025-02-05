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
                System.out.println("Invalid phone numbers");
                return 0;
            }
    
            // Get user IDs
            int currentUserId = getUserIdByPhone(currentUserPhone);
            int otherUserId = getUserIdByPhone(otherUserPhone);
            
            if (currentUserId == -1 || otherUserId == -1) {
                System.out.println("User IDs not found");
                return 0;
            }
    
            // Check for existing chat
            Integer existingChatId = findExistingSingleChat(currentUserId, otherUserId);
            if (existingChatId != 0) {  // Change here: check if not 0.
                System.out.println("Existing chat found: " + existingChatId);
                return 0;
            }
    
            // Create new chat
            con.setAutoCommit(false);
            int chatId = createNewChat(con, "SINGLE", null);
            System.out.println("New chat created with ID: " + chatId);
            linkUsersToChat(con, chatId, Arrays.asList(currentUserId, otherUserId));
            con.commit();
    
            return chatId;
            
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return 0;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    

    

 
    public int createGroup(String creatorPhone, List<String> participantPhones, String groupName) {
        int chatId = -1; 
    
        try (Connection con = dm.getConnection()) {
            List<Integer> userIds = new ArrayList<>();
    
            int creatorId = -1;
            try {
                creatorId = getUserIdByPhone(creatorPhone);
            } catch (RemoteException e) {
                System.err.println("Error fetching creator ID: " + e.getMessage());
                return -1;
            }
    
            if (creatorId == -1) return -1; 
            userIds.add(creatorId);
    
            for (String phone : participantPhones) {
                int userId = -1;
                try {
                    userId = getUserIdByPhone(phone);
                } catch (RemoteException e) {
                    System.err.println("Error fetching user ID for phone: " + phone);
                    continue; 
                }
    
                if (userId != -1) userIds.add(userId);
            }
    
            if (userIds.size() < 2) return -1; 
    
            con.setAutoCommit(false);
            chatId = createNewChat(con, "GROUP", groupName); 
            if (chatId == -1) { 
                con.rollback();
                return -1;
            }
            linkUsersToChat(con, chatId, userIds);
            con.commit();
    
            System.out.println("Group chat created with ID: " + chatId);
    
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (dm.getConnection() != null) { 
                    dm.getConnection().rollback(); 
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            chatId = -1; 
        }
        return chatId; 
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

    private int createNewChat(Connection con, String chatType, String chatName) throws SQLException {
    String query = "INSERT INTO Chat (chatType, chatName) VALUES (?, ?)";
    try (PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, chatType);
        ps.setString(2, chatName);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);
        throw new SQLException("Failed to get generated chat ID");
    }
}

private void linkUsersToChat(Connection con, int chatId, List<Integer> userIds) throws SQLException {
    String query = "INSERT INTO UserChat (chatID, userID) VALUES (?, ?)";
    try (PreparedStatement ps = con.prepareStatement(query)) {
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
        List<UserDTO> allGroups = new ArrayList<>();
    
        String query = "SELECT c.chatID, c.chatName, c.chatPicture FROM Chat c " +
                       "JOIN UserChat u ON c.chatID = u.chatID " +
                       "WHERE u.userID = ? AND c.chatType = 'GROUP'";
    
        try (Connection con = dm.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
    
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
    
            while (rs.next()) {
                UserDTO groupChat = new UserDTO();
                groupChat.setUserID(rs.getInt("chatID"));
                groupChat.setName(rs.getString("chatName"));
                
                if(rs.getString("chatPicture") != null && rs.getString("chatPicture").length()>0) {
                    groupChat.setUserPicture(images.downloadPP(rs.getString("chatPicture")));
                } else  {
                    groupChat.setUserPicture(null);
                }
                allGroups.add(groupChat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allGroups;
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
