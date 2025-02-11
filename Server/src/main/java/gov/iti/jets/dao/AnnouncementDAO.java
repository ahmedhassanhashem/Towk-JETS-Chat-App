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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AnnouncementDAO extends UnicastRemoteObject implements AnnouncementDAOInterface {

        ConcurrentHashMap<Integer,CopyOnWriteArrayList<ClientInt>> online;

    public AnnouncementDAO() throws RemoteException {
        super();
        online = new ConcurrentHashMap<>();
        //TODO Auto-generated constructor stub
    }

    @Override
    public void register(int userID,ClientInt clientRef) throws RemoteException {

        online.computeIfAbsent(userID, k -> new CopyOnWriteArrayList<>()).add(clientRef);
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
    }

    @Override
    public AnnouncementDTO create(AnnouncementDTO announcement) throws RemoteException {
        String insertQuery = "INSERT INTO Announcement (announcementTitle, announcementContent) VALUES (?, ?)";

        try (Connection con = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement insertStatement = con.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

         
            insertStatement.setString(1, announcement.getAnnouncementTitle());
            insertStatement.setString(2, announcement.getAnnouncementContent());

            int rowsAffected = insertStatement.executeUpdate();
            if (rowsAffected > 0) {
             
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    announcement.setAnnouncementID(generatedKeys.getInt(1));
                }
                for(int id : online.keySet()){
                    List<ClientInt> clients = online.get(id);
                    if (clients != null) {
                        List<ClientInt> toRemove = new ArrayList<>();
                
                        for (ClientInt client : clients) {
                            try {
                                client.sendMessage(announcement);
                            } catch (RemoteException e) {
                                toRemove.add(client); 
                            }
                        }
                
                        clients.removeAll(toRemove); 
                    }

                }
                return announcement;
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public AnnouncementDTO read(AnnouncementDTO announcement) throws RemoteException {
        String query = "SELECT * FROM Announcement WHERE announcementID = ?";
        try (Connection con = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, announcement.getAnnouncementID());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                AnnouncementDTO resultAnnouncement = new AnnouncementDTO();
                resultAnnouncement.setAnnouncementID(resultSet.getInt("announcementID"));
                resultAnnouncement.setAnnouncementTitle(resultSet.getString("announcementTitle"));
                resultAnnouncement.setAnnouncementContent(resultSet.getString("announcementContent"));
                return resultAnnouncement;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public AnnouncementDTO update(AnnouncementDTO announcement) throws RemoteException {
        String query = "UPDATE Announcement SET announcementTitle = ?, announcementContent = ? WHERE announcementID = ?";
        try (Connection con = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setString(1, announcement.getAnnouncementTitle());
            preparedStatement.setString(2, announcement.getAnnouncementContent());
            preparedStatement.setInt(3, announcement.getAnnouncementID());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return announcement;
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(AnnouncementDTO announcement) throws RemoteException {
        String query = "DELETE FROM Announcement WHERE announcementID = ?";
        try (Connection con = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, announcement.getAnnouncementID());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Announcement with ID " + announcement.getAnnouncementID() + " deleted successfully.");
            } else {
                System.out.println("Announcement deletion failed. No such announcement found.");
            }

        } catch (SQLException e) {
            System.err.println("Error while deleting the announcement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<AnnouncementDTO> findAll() {
        // ObservableList<AnnouncementDTO> announcementList = FXCollections.observableArrayList();
        List<AnnouncementDTO> announcementList = new ArrayList<>();
        String query = "SELECT * FROM Announcement";
        try (Connection con = DatabaseConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                AnnouncementDTO resultAnnouncement = new AnnouncementDTO();
                resultAnnouncement.setAnnouncementID(resultSet.getInt("announcementID"));
                resultAnnouncement.setAnnouncementTitle(resultSet.getString("announcementTitle"));
                resultAnnouncement.setAnnouncementContent(resultSet.getString("announcementContent"));
                announcementList.add(resultAnnouncement);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return announcementList;
    }

   
    
}

