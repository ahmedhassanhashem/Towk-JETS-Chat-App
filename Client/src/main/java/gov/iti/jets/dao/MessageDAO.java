package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.*;
import gov.iti.jets.dto.MessageDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MessageDAO {

    DatabaseConnectionManager dm;

    public MessageDAO() {
        dm = DatabaseConnectionManager.getInstance();

    }

    public MessageDTO create(MessageDTO msg) {
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

    public ObservableList<MessageDTO> findAllMessages(int chatID) {
        ObservableList<MessageDTO> msgList = FXCollections.observableArrayList();

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

    public String findLastMessage(int user1, int user2) {
        int chatID;
        try {
            chatID = new ChatDAO().findExistingSingleChat(user1, user2);
        } catch (SQLException e) {
            return "";

        }
        if (chatID == 0)
            return "";
        ObservableList<MessageDTO> msgList = findAllMessages(chatID);
        MessageDTO m = msgList.get(msgList.size() - 1);
        String ret = m.getMessageContent();
        if (ret.length() > 7)
            ret = ret.substring(0, 7) + "...";
        return ret;

    }

}
