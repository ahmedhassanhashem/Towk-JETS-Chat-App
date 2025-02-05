package gov.iti.jets.dao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.WebRowSet;

import gov.iti.jets.dto.MessageDTO;

public class MessageDAO extends UnicastRemoteObject implements MessageDAOInterface{

    DatabaseConnectionManager dm;

    public MessageDAO() throws RemoteException {
        super();
        dm = DatabaseConnectionManager.getInstance();

    }

    public MessageDTO create(MessageDTO msg) throws RemoteException {
        String msgContent = msg.getMessageContent();
        int chatID = msg.getChatID();
        int userID = msg.getUserID();
        java.sql.Date msgDate = msg.getMessageDate();
        int attachID = msg.getAttachmentID();

        if (msgContent.isBlank() && attachID ==0)
            return null;

        String query = "INSERT INTO Message (messageContent, chatID, userID, messageDate, attachmentID) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = dm.getConnection();
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setString(1, msgContent);
            ps.setInt(2, chatID);
            ps.setInt(3, userID);
            ps.setDate(4, msgDate);
            if (attachID <= 0)
                ps.setNull(5, java.sql.Types.INTEGER);
            else
                ps.setInt(5, attachID);
            ps.executeUpdate();
            System.out.println("Message inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public List<MessageDTO> findAllMessages(int chatID) throws RemoteException {
        // ObservableList<MessageDTO> msgList = FXCollections.observableArrayList();
        List<MessageDTO> msgList = new ArrayList<>();
        String query = "SELECT * FROM Message WHERE chatID = ?;";

        try (Connection con = dm.getConnection();
                WebRowSet rs = RowSetProvider.newFactory().createWebRowSet();) {
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

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return msgList;
    }

    public String findLastMessageGroup(int chatID) throws RemoteException {
        // System.out.println("findLastMessageGroup: Method called with chatID = " + chatID);
        try {
            List<MessageDTO> msgList = findAllMessages(chatID);
            if (msgList == null || msgList.isEmpty()) {
                // System.out.println("findLastMessageGroup: No messages found!");
                return "";
            }
            MessageDTO m = msgList.get(msgList.size() - 1); 
            String ret = m.getMessageContent();
        
            // if (ret != null && ret.length() > 20) { 
            //     ret = ret.substring(0, 20) + "...";
            // }
            return ret != null ? ret : ""; 
        } catch (Exception e) {
            // System.out.println("findLastMessageGroup: Caught an exception!");
            e.printStackTrace();
            return "";
        }
    }
    
    
    
    public MessageDTO findLastMessage(int chatID) throws RemoteException {
        if (chatID == 0) {
            return null;
        }
    
        List<MessageDTO> msgList = findAllMessages(chatID);
        System.out.println(chatID);
        if (msgList == null || msgList.isEmpty()) {  
            //System.out.println("findLastMessage: No messages found for chatID = " + chatID);
            return null;
        }
    
        int lastIndex = msgList.size() - 1;
        MessageDTO m = msgList.get(lastIndex); 
        if (lastIndex < 0) {
            return null;
        }
    
        return m;
    }
    public String findLastMessage(int user1, int user2) throws RemoteException{
        int chatID;
        try {
            chatID = new ChatDAO().findExistingSingleChat(user1, user2);
        } catch (SQLException e) {
            return "";

        }
        if (chatID == 0)
            return "";
            
        List<MessageDTO> msgList = findAllMessages(chatID);
        if(!msgList.isEmpty()){
            MessageDTO m = msgList.get(msgList.size() - 1);
        String ret = m.getMessageContent();
        if (ret.length() > 20)
            ret = ret.substring(0, 20) + "...";
        return ret;
        }
        return "";
    }

}
