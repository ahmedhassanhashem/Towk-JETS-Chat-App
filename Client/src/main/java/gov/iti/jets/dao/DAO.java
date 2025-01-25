package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import gov.iti.jets.dto.ContactDTO;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.UserChatDTO;
import gov.iti.jets.dto.UserDTO;
import javafx.collections.ObservableList;

public interface DAO<T>{



    //////// CRUD --> create = insert,  read = get;
    T create(T t);


    T read(T t);


    T update(T t);


    void delete(T t);

    
    List<T> findAll();

    public default int getUserIdByPhone(String phone) throws SQLException {
        String query = "SELECT userID FROM User WHERE phone = ?";
        try (Connection con = DatabaseConnectionManager.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("userID") : -1;
        }
    }


    // message
    public default ObservableList<MessageDTO> findAllMessages(int chatID){return null;}

    // contact
    public default String create(String senderPhone, String receiverPhone) {return null;}
    public default ObservableList<UserDTO> findAllContactsACCEPTED(String phone){return null;}
    public default ObservableList<UserDTO> findAllContactsPENDING(String phone){return null;}
    public default ObservableList<UserDTO> findAllContactsREJECTED(String phone){return null;}

    // chat
    public default String createSingle(String currentUserPhone, String otherUserPhone){return null;}
    public default String createGroup(String creatorPhone, List<String> participantPhones, String groupName){return null;}
    public default int read(String userPhone){return 0;}
    



}
