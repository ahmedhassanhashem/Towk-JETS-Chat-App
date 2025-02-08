package gov.iti.jets.dao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.WebRowSet;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.NotificationDTO;

public class MessageDAO extends UnicastRemoteObject implements MessageDAOInterface{

    DatabaseConnectionManager dm;
    HashMap<Integer,ArrayList<ClientInt>> online;
    HashMap<Integer,ArrayList<ClientInt>> onlineChatCard;
    NotificationDAO notificationDAO;

    public MessageDAO() throws RemoteException {
        super();
        dm = DatabaseConnectionManager.getInstance();
        online = new HashMap<>();
        onlineChatCard=new HashMap<>();
        // notificationDAO = new NotificationDAO();

    }
    @Override
    public void registerChat(int chatID,ClientInt clientRef) throws RemoteException {

        onlineChatCard.computeIfAbsent(chatID, k -> new ArrayList<>()).add(clientRef);


    }

    public void setNotDao(NotificationDAO notificationDAO){
        this.notificationDAO = notificationDAO;
    }
    // Unregister a client
    @Override
    public void unRegisterChat(int chatID,ClientInt clientRef) throws RemoteException {

        if (onlineChatCard.containsKey(chatID)) {
            List<ClientInt> clientList = onlineChatCard.get(chatID);
            
            clientList.remove(clientRef);
            
            if (clientList.isEmpty()) {
                online.remove(chatID);
            }
        }
    }
    @Override
    public void register(int chatID,ClientInt clientRef) throws RemoteException {

        online.computeIfAbsent(chatID, k -> new ArrayList<>()).add(clientRef);

    }

    // Unregister a client
    @Override
    public void unRegister(int chatID,ClientInt clientRef) throws RemoteException {

        if (online.containsKey(chatID)) {
            List<ClientInt> clientList = online.get(chatID);
            
            clientList.remove(clientRef);
            
            if (clientList.isEmpty()) {
                online.remove(chatID);
            }
        }
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
        String sql ="select * from Message order by messageID desc limit 1 ;";
        try (Connection con = dm.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                ) {
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
            ResultSet re = preparedStatement.executeQuery();
            if(re.next())
            msg.setMesssageID(re.getInt("messageID"));

            List<Integer> onlineUserIDs = new ArrayList<>();
            for(ClientInt c: online.get(msg.getChatID()) ){
                c.sendMessage(msg);
                onlineUserIDs.add(c.get());
            }
            for(ClientInt c:onlineChatCard.get(msg.getChatID())){
                c.sendMessage(msg);
            }
            sendToRest(onlineUserIDs, msg);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msg;
    }
    private void sendToRest(List<Integer> userIDs,MessageDTO msg ){
                String sql = "select * from UserChat where chatID = ? ;";
        List<Integer> participants = new ArrayList<>();

        try (Connection con = dm.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sql);) {

            preparedStatement.setInt(1, msg.getChatID());
            ResultSet re = preparedStatement.executeQuery();

            while (re.next()) {
                participants.add(re.getInt("userID"));
            }
            participants.removeAll(userIDs);
            for(Integer i : participants){

                NotificationDTO nDto =new NotificationDTO();
                nDto.setUserID(i);
                nDto.setMessageID(msg.getMesssageID());
                // System.out.println(i + " " + msg.getMesssageID());
                try {
                    notificationDAO.create(nDto);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

  
            

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                msg.setMesssageID(rs.getInt("messageID"));

                msgList.add(msg);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return msgList;
    }

    @Override
    public MessageDTO read(int msgID) throws RemoteException{
        String query = "SELECT * FROM Message WHERE messageID = ?;";

        try (Connection con = dm.getConnection();
                WebRowSet rs = RowSetProvider.newFactory().createWebRowSet();) {
            rs.setCommand(query);
            rs.setInt(1, msgID);
            rs.execute(con);

            if (rs.next()) {
                MessageDTO msg = new MessageDTO();
                msg.setMessageContent(rs.getString("messageContent"));
                msg.setChatID(rs.getInt("chatID"));
                msg.setUserID(rs.getInt("userID"));
                msg.setMessageDate(rs.getDate("messageDate"));
                msg.setAttachmentID(rs.getInt("attachmentID"));
                msg.setMesssageID(msgID);

                return msg;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
           // System.out.println("Original Content from dao: " + ret);
        // if (ret.length() > 20)
        //     ret = ret.substring(0, 20) + "...";
        return ret;
        }
        return "";
    }

}
