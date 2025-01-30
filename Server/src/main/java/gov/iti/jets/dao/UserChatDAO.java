package gov.iti.jets.dao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import gov.iti.jets.server.Images;

public class UserChatDAO extends UnicastRemoteObject implements UserChatDAOInterface{

    DatabaseConnectionManager dm;
    Images images = new Images();
    public UserChatDAO() throws RemoteException{
        super();
        dm = DatabaseConnectionManager.getInstance();

    }



    public List<UserDTO> findAll(int userId) throws RemoteException{
        // ObservableList<UserDTO> allChatUsers = FXCollections.observableArrayList();
         List<UserDTO> allChatUsers = new ArrayList<>();
        String query = " SELECT distinct *\n" + //
                        "FROM UserChat uc\n" + //
                        "JOIN User u ON uc.userID = u.userID\n" + //
                        "WHERE uc.chatID IN (\n" + //
                        "    SELECT chatID\n" + //
                        "    FROM UserChat\n" + //
                        "    WHERE userID = ?\n" + //
                        ")\n" + //
                        "AND uc.userID <> ?\n" + //
                        "AND uc.chatID IN (\n" + //
                        "    SELECT chatID\n" + //
                        "    FROM Chat\n" + //
                        "    WHERE chatType = 'SINGLE'\n" + //
                        ");";
        try (Connection con = dm.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
        UserDTO user = new UserDTO();
            
            while (true) {
                user = convert(rs);
                if(user == null)break;
                allChatUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allChatUsers;
        // TODO Auto-generated method stub
        
    }



    public List<Integer> getChatParticipants(int chatId) throws RemoteException {
        // ObservableList<Integer> participants = FXCollections.observableArrayList();
        List<Integer> participants = new ArrayList<>();

        String query = "SELECT userID FROM UserChat WHERE chatID = ?";

        try (Connection con = dm.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, chatId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                participants.add(rs.getInt("userID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }


    

    public List<Integer> getUserChats(int userId) throws RemoteException {
        // ObservableList<Integer> chats = FXCollections.observableArrayList();
        List<Integer> chats = new ArrayList<>();

        String query = "SELECT chatID FROM UserChat WHERE userID = ?";

        try (Connection con = dm.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                chats.add(rs.getInt("chatID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }

        private UserDTO convert(ResultSet re) {
        UserDTO user = new UserDTO();
        try {
            if (!re.next())
                return null;
            // re.next();
            user.setUserID(re.getInt("userID"));
            user.setPhone(re.getString("phone"));
            user.setName(re.getString("name"));
            user.setCountry(re.getString("country"));
            user.setGender(Gender.valueOf(re.getString("gender")));
            user.setEmail(re.getString("email"));
            user.setBirthdate(re.getDate("birthdate"));
            user.setPassword(re.getString("password"));
            user.setFirstLogin(re.getBoolean("firstLogin"));
            user.setUserStatus(UserStatus.valueOf(re.getString("userStatus")));
            user.setUserMode(UserMode.valueOf(re.getString("userMode")));
            

            try {
                user.setUserMode(UserMode.valueOf(re.getString("userMode")));
            } catch (IllegalArgumentException | NullPointerException e) {
                user.setUserMode(null);
            }
            try {
                user.setBio(re.getString("bio"));
            } catch (IllegalArgumentException | NullPointerException e) {
                user.setBio(null);
            }
            if(re.getString("userPicture") != null && re.getString("userPicture").length()>0) {
                user.setUserPicture(images.downloadPP(re.getString("userPicture")));
            } else  {
                user.setUserPicture(null);
            }

            // user.setUserPicture(images.downloadPP(re.getString("userPicture")));
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
