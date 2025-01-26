package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.iti.jets.dto.ChatDTO;
import gov.iti.jets.dto.ChatType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatDAO{

    DatabaseConnectionManager dm;
    UserChatDAO userChatDAO = new UserChatDAO();
    MessageDAO messageDAO = new MessageDAO();


    public ChatDAO() {
        dm = DatabaseConnectionManager.getInstance();
    }


    public String createSingle(String currentUserPhone, String otherUserPhone) {
        Connection con = dm.getConnection();
        try {
            
            if (currentUserPhone == null || otherUserPhone == null 
                || currentUserPhone.length() != 11 || otherUserPhone.length() != 11) {
                return "Invalid phone numbers";
            }

            // Get user IDs
            int currentUserId = getUserIdByPhone(currentUserPhone);
            int otherUserId = getUserIdByPhone(otherUserPhone);
            
            if (currentUserId == -1 || otherUserId == -1) {
                return "User not found";
            }

            // Check for existing chat
            Integer existingChatId = findExistingSingleChat(currentUserId, otherUserId);
            if (existingChatId != null) {
                return "Chat already exists with ID: " + existingChatId;
            }

            // Create new chat
            con.setAutoCommit(false);
            int chatId = createNewChat("SINGLE", null);
            linkUsersToChat(chatId, Arrays.asList(currentUserId, otherUserId));
            con.commit();

            return "Chat created successfully with ID: " + chatId;
            
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return "Error creating chat: " + e.getMessage();
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    

 
    public String createGroup(String creatorPhone, List<String> participantPhones, String groupName) {
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

    private Integer findExistingSingleChat(int user1, int user2) throws SQLException {
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
            return rs.next() ? rs.getInt("chatID") : null;
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



    public int getUserIdByPhone(String phone) throws SQLException {
        String query = "SELECT userID FROM User WHERE phone = ?";
        try (Connection con = dm.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("userID") : -1;
        }
    }

}
