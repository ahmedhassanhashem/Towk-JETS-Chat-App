package gov.iti.jets.dao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import gov.iti.jets.dto.AttachementDTO;

public class AttachementDAO  extends UnicastRemoteObject implements AttachementDAOInterface{
    DatabaseConnectionManager meh;

    public AttachementDAO() throws RemoteException{
        super();
        meh = DatabaseConnectionManager.getInstance();
    }

    @Override
    public String getAttachmentTitle(int id) throws RemoteException{
        if (id < 1)
            return "";
        String sql = "Select attachmentTitle From Attachment where attachmentID = ?";
        ResultSet re;
        try (Connection con = meh.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sql);) {

            preparedStatement.setInt(1, id);
            re = preparedStatement.executeQuery();
            re.next();
            return re.getString(1);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;

    }
    
    @Override
    public int getAttachmentID(String title) throws RemoteException{
        if (title.length() < 1)
            return 0;
        String sql = "Select attachmentID From Attachment where attachmentTitle = ?";
        ResultSet re;
        try (Connection con = meh.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sql);) {

            preparedStatement.setString(1, title);
            re = preparedStatement.executeQuery();
            re.next();
            return re.getInt(1);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return 0;

    }
    // String phone =user.getPhone(); String name = user.getName(); String country =
    // user.getCountry();
    // Gender gender = user.getGender(); String email = user.getEmail(); Date
    // birthdate = user.getBirthdate();
    // String password =user.getPassword(); Boolean firstLogin = true; UserStatus
    // userStatus = UserStatus.ONLINE;

    // if(phone.length() !=11 || name.length() ==0 || password.length() ==0 ||
    // birthdate ==null )return null;
    // String sql2 = "INSERT INTO `User` (`phone`, `name` , `country`, `gender`,
    // `email`, `birthdate`,`password`, `firstLogin`, `userStatus`)
    // VALUES(?,?,?,?,?,?,?,?,?)";
    // try (Connection con = meh.getConnection();
    // PreparedStatement preparedStatement = con.prepareStatement(sql2);){

    // java.sql.Date birthdate2= java.sql.Date.valueOf(birthdate.toString());
    // preparedStatement.setString(1, phone);
    // preparedStatement.setString(2, name);
    // preparedStatement.setString(3, country);
    // preparedStatement.setString(4, gender.toString());
    // preparedStatement.setString(5, email);
    // preparedStatement.setDate(6, birthdate2);
    // preparedStatement.setString(7, password);
    // preparedStatement.setBoolean(8, firstLogin);
    // preparedStatement.setString(9, userStatus.toString());
    // preparedStatement.executeUpdate();
    // System.out.println("Record inserted successfully.");
    // return user;
    // @Override
    // public AttachementDTO createAttachment(AttachementDTO attachementDTO) throws RemoteException{
    //     String title = attachementDTO.getAttachmentTitle();
    //     String type = attachementDTO.getAttachmentType();
    //     long size = attachementDTO.getAttachmentSize();

    //     String sql = "INSERT INTO `Attachment` (`attachmentTitle`, `attachmentType`, `attachmentSize`)\n" + //
    //             "VALUES\n" + //
    //             "(?, ?, ?);";
    //     ResultSet re;
    //     try (Connection con = meh.getConnection();
    //             PreparedStatement preparedStatement = con.prepareStatement(sql);) {
    //                 preparedStatement.setString(1, title);
    //                 preparedStatement.setString(2, type);
    //                 preparedStatement.setLong(3, size);
    //                 preparedStatement.executeUpdate();
    //                 Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    //                 re = stmt.executeQuery("SELECT * FROM Attachment");
    //                 re.last();
    //                 attachementDTO.setAttachmentID(re.getInt("attachmentID"));

    //         return attachementDTO;
    //     } catch (SQLException e) {

    //         e.printStackTrace();
    //     }
    //     return null;

    // }

    @Override
    public AttachementDTO createAttachment(AttachementDTO attachementDTO) throws RemoteException {
        String title = attachementDTO.getAttachmentTitle();
        String type = attachementDTO.getAttachmentType();
        long size = attachementDTO.getAttachmentSize();

        String sql = "INSERT INTO Attachment (attachmentTitle, attachmentType, attachmentSize) VALUES (?, ?, ?)";

        try (Connection con = meh.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, type);
            preparedStatement.setLong(3, size);

            int affectedRows = preparedStatement.executeUpdate();

            // Ensure a record was inserted
            if (affectedRows == 0) {
                throw new SQLException("Creating attachment failed, no rows affected.");
            }

            // Retrieve the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    attachementDTO.setAttachmentID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating attachment failed, no ID obtained.");
                }
            }

            return attachementDTO;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
