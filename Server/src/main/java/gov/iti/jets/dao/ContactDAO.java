package gov.iti.jets.dao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import gov.iti.jets.server.Images;

public class ContactDAO extends UnicastRemoteObject implements ContactDAOInterface{

    DatabaseConnectionManager dm;
    Images images = new Images();

    public ContactDAO() throws RemoteException {
        super();
        dm = DatabaseConnectionManager.getInstance();
    }

    public String create(String senderPhone, String receiverPhone) throws RemoteException {
        if (receiverPhone == null) {
            return "Empty phone number";
        }
        if (senderPhone.length() != 11 || receiverPhone.length() != 11) {
            return "Invalid phone number format";
        }

        if (checkSent(senderPhone, receiverPhone)) {
            return "Request already Sent!";
        }
        if (senderPhone.equals(receiverPhone)) {
            return "Can't send to myself!";
        }

        String query = "INSERT INTO UserContact (senderID, receiverID, requestStatus) "
                + "VALUES ("
                + "    (SELECT userID FROM User WHERE phone = ?), "
                + "    (SELECT userID FROM User WHERE phone = ?), "
                + "    'PENDING'"
                + ")";

        try (Connection con = dm.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {

            if (!userExists(senderPhone)) {
                System.out.println("Sender account not found");
                return "Sender account not found";
            }
            if (!userExists(receiverPhone)) {
                System.out.println("Receiver account not found");
                return "Receiver account not found";
            }

            ps.setString(1, senderPhone);
            ps.setString(2, receiverPhone);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return "Sent Successfully";
            }
            return "Failed to send request";

        } catch (SQLIntegrityConstraintViolationException e) {
            return "Cannot send request - user does not exist";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
    }

    public boolean checkSent(String senderPhone, String receiverPhone) throws RemoteException {

        String query = "select * from UserContact where senderID =(select userID from User where phone = ?) and receiverID = (select userID from User where phone = ?);";

        try (Connection con = dm.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, senderPhone);
            ps.setString(2, receiverPhone);
            ResultSet re = ps.executeQuery();
            return re.isBeforeFirst();

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean userExists(String phone) throws SQLException,RemoteException {
        String query = "SELECT 1 FROM User WHERE phone = ?";
        try (Connection con = dm.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // to get all the contacts to show in the contacts list
    public List<UserDTO> findAllContactsACCEPTED(String userPhone) throws RemoteException {
        // ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
                 List<UserDTO> contacts = new ArrayList<>();

        String getUserIDQuery = "SELECT userID FROM User WHERE phone = ?";
        String getContactsQuery
                = "(SELECT u.* FROM UserContact uc JOIN User u ON uc.receiverID = u.userID "
                + "WHERE uc.senderID = ? AND uc.requestStatus = 'ACCEPT') "
                + "UNION "
                + "(SELECT u.* FROM UserContact uc JOIN User u ON uc.senderID = u.userID "
                + "WHERE uc.receiverID = ? AND uc.requestStatus = 'ACCEPT')";

        try (Connection con = dm.getConnection(); PreparedStatement psGetUserID = con.prepareStatement(getUserIDQuery)) {

            psGetUserID.setString(1, userPhone);
            ResultSet rsUser = psGetUserID.executeQuery();
            if (!rsUser.next()) {
                return contacts; // User not found
            }
            int userID = rsUser.getInt("userID");

            try (PreparedStatement psContacts = con.prepareStatement(getContactsQuery)) {
                psContacts.setInt(1, userID);
                psContacts.setInt(2, userID);
                ResultSet re = psContacts.executeQuery();

                while (re.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserID(re.getInt("userID"));
                    user.setPhone(re.getString("phone"));
                    user.setName(re.getString("name"));
                    user.setCountry(re.getString("country"));
                    user.setGender(Gender.valueOf(re.getString("gender")));
                    user.setEmail(re.getString("email"));
                    user.setBirthdate(re.getDate("birthdate"));
                    // user.setPassword(re.getString("password"));
                    // user.setFirstLogin(re.getBoolean("firstLogin"));
                    user.setUserStatus(UserStatus.valueOf(re.getString("userStatus")));

                    String mode = re.getString("userMode");
                    if (mode == null) {
                        user.setUserMode(UserMode.AVAILABLE); 
                    }else {
                        user.setUserMode(UserMode.valueOf(re.getString("userMode")));
                    }

                    String bio = re.getString("bio");
                    if (bio != null) {
                        user.setBio(bio); 
                    }else {
                        user.setBio("Hi im using towk!");
                    }

                    // need modification 
                    // byte[] pic = images.downloadPP(re.getString(("userPicture")));
                    // if(bio == null)
                    //         /// default pic will set here 
                    //     user.setUserPicture(pic);
                    // else
                    //     user.setUserPicture(pic);
                    if (re.getString("userPicture") != null && re.getString("userPicture").length() > 0) {
                        user.setUserPicture(images.downloadPP(re.getString("userPicture")));
                    } else {
                        user.setUserPicture(null);
                    }
                    contacts.add(user);

                }
                return contacts;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    // to get all the rejected to show in the notfication that you are rejected
    public List<UserDTO> findAllContactsREJECTED(String userPhone) throws RemoteException {
        // ObservableList<UserDTO> rejectedContacts = FXCollections.observableArrayList();
        List<UserDTO> rejectedContacts = new ArrayList<>();

        String query = " SELECT u.userID, u.phone, u.name, u.country, u.userStatus "
                + "        FROM UserContact uc"
                + "        JOIN User u ON uc.receiverID = u.userID "
                + "        WHERE uc.senderID = ? "
                + //
                "        AND uc.requestStatus = 'REJECT'";

        try (Connection con = dm.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT userID FROM User WHERE phone = ?")) {

            ps.setString(1, userPhone);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return rejectedContacts;
            }

            int currentUserId = rs.getInt("userID");

            try (PreparedStatement psRejected = con.prepareStatement(query)) {
                psRejected.setInt(1, currentUserId);

                ResultSet re = psRejected.executeQuery();
                while (re.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserID(re.getInt("userID"));
                    user.setPhone(re.getString("phone"));
                    user.setName(re.getString("name"));
                    user.setCountry(re.getString("country"));
                    user.setGender(Gender.valueOf(re.getString("gender")));
                    user.setEmail(re.getString("email"));
                    user.setBirthdate(re.getDate("birthdate"));
                    // user.setPassword(re.getString("password"));
                    // user.setFirstLogin(re.getBoolean("firstLogin"));
                    user.setUserStatus(UserStatus.valueOf(re.getString("userStatus")));
                    user.setUserMode(UserMode.valueOf(re.getString("userMode")));
                    rejectedContacts.add(user);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rejectedContacts;
    }

    // to get all the pending request so you can list in the notfication to accept or reject
    public List<UserDTO> findAllContactsPENDING(String userPhone) throws RemoteException{
        // ObservableList<UserDTO> pendingReceived = FXCollections.observableArrayList();
        List<UserDTO> pendingReceived = new ArrayList<>();

        String query = "SELECT u.* "
                + "        FROM UserContact uc"
                + "        JOIN User u ON uc.senderID = u.userID "
                + "        WHERE uc.receiverID = ? "
                + "        AND uc.requestStatus = 'PENDING'"
                + "        ORDER BY u.name ASC";

        try (Connection con = dm.getConnection(); PreparedStatement psGetUser = con.prepareStatement("SELECT userID FROM User WHERE phone = ?")) {

            // Get current user's ID (the receiver)
            psGetUser.setString(1, userPhone);
            ResultSet rsUser = psGetUser.executeQuery();
            if (!rsUser.next()) {
                return pendingReceived;
            }

            int currentUserId = rsUser.getInt("userID");

            // Get pending requests sent to current user
            try (PreparedStatement psPending = con.prepareStatement(query)) {
                psPending.setInt(1, currentUserId);

                ResultSet re = psPending.executeQuery();
                while (re.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserID(re.getInt("userID"));
                    user.setPhone(re.getString("phone"));
                    user.setName(re.getString("name"));
                    user.setCountry(re.getString("country"));
                    user.setGender(Gender.valueOf(re.getString("gender")));
                    user.setEmail(re.getString("email"));
                    user.setBirthdate(re.getDate("birthdate"));
                    // user.setPassword(re.getString("password"));
                    // user.setFirstLogin(re.getBoolean("firstLogin"));
                    user.setUserStatus(UserStatus.valueOf(re.getString("userStatus")));
                    user.setUserMode(UserMode.valueOf(re.getString("userMode")));
                    pendingReceived.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingReceived;

    }

}