package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import gov.iti.jets.dto.AnnouncementDTO;

public class AnnouncementDAO {

    
    public AnnouncementDTO create(AnnouncementDTO announcement) {
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
                return announcement;
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public AnnouncementDTO read(AnnouncementDTO announcement) {
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

    public AnnouncementDTO update(AnnouncementDTO announcement) {
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


    public void delete(AnnouncementDTO announcement) {
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

    
    public List<AnnouncementDTO> findAll() {
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

