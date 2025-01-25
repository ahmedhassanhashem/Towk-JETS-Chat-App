package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import gov.iti.jets.dto.UserChatDTO;
import gov.iti.jets.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserChatDAO implements DAO<UserChatDTO>{

    DatabaseConnectionManager dm;
    Connection con;

    public UserChatDAO() {
        dm = DatabaseConnectionManager.getInstance();
        con = dm.getConnection();

    }


    @Override
    public UserChatDTO create(UserChatDTO t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public UserChatDTO read(UserChatDTO t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public UserChatDTO update(UserChatDTO t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(UserChatDTO t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<UserChatDTO> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    public ObservableList<Integer> getChatParticipants(int chatId) {
        ObservableList<Integer> participants = FXCollections.observableArrayList();
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

    public ObservableList<Integer> getUserChats(int userId) {
        ObservableList<Integer> chats = FXCollections.observableArrayList();
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


}

