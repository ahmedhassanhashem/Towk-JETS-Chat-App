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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.AnnouncementDTO;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.NotificationDTO;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NotificationDAO extends UnicastRemoteObject implements NotificationDAOInterface {
    DatabaseConnectionManager dm;
    ConcurrentHashMap<Integer,CopyOnWriteArrayList<ClientInt>> online;

    public NotificationDAO() throws RemoteException {
        super();
        dm = DatabaseConnectionManager.getInstance();
        online = new ConcurrentHashMap<>();

        // TODO Auto-generated constructor stub
    }

    @Override
    public void register(int userID,ClientInt clientRef) throws RemoteException {

        online.computeIfAbsent(userID, k -> new CopyOnWriteArrayList<>()).add(clientRef);
        // System.out.println(online);
    }

    // Unregister a client
    @Override
    public void unRegister(int userID,ClientInt clientRef) throws RemoteException {

        if (online.containsKey(userID)) {
            List<ClientInt> clientList = online.get(userID);
            
            clientList.remove(clientRef);
            
            if (clientList.isEmpty()) {
                online.remove(userID);
            }
        }
        // System.out.println("bye ->> "+online);
    }

    @Override
    public NotificationDTO create(NotificationDTO not) throws RemoteException {

        if(not == null)return null;
        int userID = not.getUserID();
        int msgID = not.getMessageID();
        NotificationDTO notificationDTO = new NotificationDTO();
        String query = "INSERT INTO Notification (userID, messageID) VALUES ( ?, ?)";
        String sql2 = "select * from Notification where userID = ? and messageID = ? ;";
        try (Connection con = dm.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                PreparedStatement preparedStatement = con.prepareStatement(sql2);
                ) {
            ps.setInt(1, userID);
            ps.setInt(2, msgID);

            ps.executeUpdate();
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, msgID);
            ResultSet re = preparedStatement.executeQuery();
            notificationDTO = convert(re);
            if(online.get(notificationDTO.getUserID())!= null && notificationDTO !=null){

                for(ClientInt c:online.get(notificationDTO.getUserID())){
                    c.sendMessage(notificationDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notificationDTO;
    }

    @Override
    public void delete(int userID,int messageID) throws RemoteException {

        String query = "DELETE FROM Notification where userID = ? AND messageID = ? ;";
        try (Connection con = dm.getConnection();
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setInt(1, userID);
            ps.setInt(2, messageID);

            int suc = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<NotificationDTO> getNotifications(int userID) throws RemoteException{

        String sql2 = "select * from Notification where userID = ? ;";
        List<NotificationDTO> notList = new ArrayList<>();

        try (Connection con = dm.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sql2);) {

            preparedStatement.setInt(1, userID);
            ResultSet re = preparedStatement.executeQuery();
            NotificationDTO nDto = convert(re);

            while(nDto !=null){
                notList.add(nDto);
                nDto = convert(re);
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notList;
    }

    @Override
    public boolean isSeen(int msgID) throws RemoteException{

        String sql2 = "select * from Notification where messageID = ? ;";

        try (Connection con = dm.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sql2);) {

            preparedStatement.setInt(1, msgID);
            ResultSet re = preparedStatement.executeQuery();

                    return !re.isBeforeFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private NotificationDTO convert(ResultSet re) {
        NotificationDTO notification = new NotificationDTO();
        try {
            if (!re.next()) {
                return null;
            }
            // re.next();
            notification.setNotificationID(re.getInt("id"));
            notification.setUserID(re.getInt("userID"));
            notification.setMessageID(re.getInt("messageID"));

            return notification;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

class oas{
    public static void main(String[] args) {
        try {
        NotificationDAO n = new NotificationDAO();
            n.create(null);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}