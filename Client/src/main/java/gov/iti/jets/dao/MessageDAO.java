package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.*;
import gov.iti.jets.dto.MessageDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MessageDAO implements DAO<MessageDTO>{

    DatabaseConnectionManager dm;
    Connection con;
    WebRowSet rs = null;

    public MessageDAO() {
        dm = DatabaseConnectionManager.getInstance();
        con = dm.getConnection();
        try {
            rs = RowSetProvider.newFactory().createWebRowSet();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public MessageDTO create(MessageDTO msg) {
        String msgContent = msg.getMessageContent();
        int chatID = msg.getChatID();
        int userID = msg.getUserID();
        java.sql.Date msgDate = msg.getMessageDate();  
        int attachID = msg.getAttachmentID();

        if(msgContent.isBlank() && attachID < 1)
            return null;

        String query = "INSERT INTO message (messageContent, chatID, userID, messageDate, attachmentID) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement ps = con.prepareStatement(query);){
            ps.setString(1, msgContent);
            ps.setInt(2, chatID);
            ps.setInt(3, userID);
            ps.setDate(4, msgDate);
            ps.setInt(5, attachID);
            ps.executeUpdate();
            System.out.println("Message inserted successfully.");
        } catch (SQLException e){
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public MessageDTO read(MessageDTO t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public MessageDTO update(MessageDTO t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(MessageDTO t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<MessageDTO> findAll() {throw new UnsupportedOperationException("");}

    @Override
    public ObservableList<MessageDTO> findAllMessages(int chatID) {
        ObservableList<MessageDTO> msgList = FXCollections.observableArrayList();

        String query = "SELECT * FROM message WHERE chatID = ?;";

        try{
            rs.setCommand(query);
            rs.setInt(1, chatID);
            rs.execute(con);

            while (rs.next()) {
                MessageDTO msg = new MessageDTO();
                msg.setMessageContent(rs.getString("messageContent"));
                msg.setChatID(rs.getInt("chatID"));
                msg.setUserID(rs.getInt("userID"));
                msg.setMessageDate(rs.getDate("messageDate"));
                msg.setAttachmentID(rs.getInt("attachmentID"));
                
                msgList.add(msg);
            }

        }catch(SQLException e){e.printStackTrace();}


        return msgList;
    }
    
}

