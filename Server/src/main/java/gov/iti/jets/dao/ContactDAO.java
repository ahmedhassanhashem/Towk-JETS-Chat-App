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

import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.WebRowSet;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import gov.iti.jets.mail.JakartaMail;
import gov.iti.jets.server.Images;

public class ContactDAO extends UnicastRemoteObject implements ContactDAOInterface{
    
    DatabaseConnectionManager dm;
    Images images = new Images();
    NotificationDAO notificationDAO;

    public ContactDAO() throws RemoteException {
        super();
        dm = DatabaseConnectionManager.getInstance();
    }
    public void setNotDao(NotificationDAO notificationDAO){
        this.notificationDAO = notificationDAO;
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
            if (checkSent(receiverPhone,senderPhone)) {
                acceptContactRequest(receiverPhone,senderPhone);
                acceptContactRequest(senderPhone,receiverPhone);

                return "Sent Successfully";
            }
            ps.setString(1, senderPhone);
            ps.setString(2, receiverPhone);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                UserDTO userDTO = convert(senderPhone);
                int id = readID(receiverPhone);
                if(notificationDAO.online.get(id)!= null && userDTO != null ){

                for(ClientInt c:notificationDAO.online.get(id)){
                    c.sendMessage(userDTO);
                }

                
            }

            JakartaMail.mailService(senderPhone, readEmail(receiverPhone), readName(senderPhone));
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

    
    private int readID(String phone){
           String query = "SELECT * FROM User WHERE phone = ?;";

        try (Connection con = dm.getConnection();
                WebRowSet rs = RowSetProvider.newFactory().createWebRowSet();) {
            rs.setCommand(query);
            rs.setString(1, phone);
            rs.execute(con);

            if (rs.next()) {


                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
            return false;
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
                    if(re.getString("userMode") != null)
                    user.setUserMode(UserMode.valueOf(re.getString("userMode")));
                    pendingReceived.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pendingReceived;

    }

    public boolean acceptContactRequest(String receiverPhone, String senderPhone) throws RemoteException {
        String getIdQuery = "SELECT id FROM UserContact " +
                            "WHERE receiverID = (SELECT userID FROM User WHERE phone = ?) " +
                            "AND senderID = (SELECT userID FROM User WHERE phone = ?) " +
                            "AND requestStatus = 'PENDING'";
        String updateQuery = "UPDATE UserContact SET requestStatus = 'ACCEPT' WHERE id = ?";
    
        try (Connection con = dm.getConnection();
             PreparedStatement getIdPs = con.prepareStatement(getIdQuery);
             PreparedStatement updatePs = con.prepareStatement(updateQuery)) {
    
            getIdPs.setString(1, receiverPhone);
            getIdPs.setString(2, senderPhone);
            ResultSet rs = getIdPs.executeQuery();
    
            if (rs.next()) {
                int requestId = rs.getInt("id");
    
                updatePs.setInt(1, requestId);
                int affectedRows = updatePs.executeUpdate();
                return affectedRows > 0;
            } else {
                return false; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean rejectContactRequest(String receiverPhone, String senderPhone) throws RemoteException {
        String getIdQuery = "SELECT id FROM UserContact " +
                            "WHERE receiverID = (SELECT userID FROM User WHERE phone = ?) " +
                            "AND senderID = (SELECT userID FROM User WHERE phone = ?) " +
                            "AND requestStatus = 'PENDING'";
        String updateQuery = "UPDATE UserContact SET requestStatus = 'REJECT' WHERE id = ?";
    
        try (Connection con = dm.getConnection();
             PreparedStatement getIdPs = con.prepareStatement(getIdQuery);
             PreparedStatement updatePs = con.prepareStatement(updateQuery)) {
    
            getIdPs.setString(1, receiverPhone);
            getIdPs.setString(2, senderPhone);
            ResultSet rs = getIdPs.executeQuery();
    
            if (rs.next()) {
                int requestId = rs.getInt("id");
                updatePs.setInt(1, requestId);
                int affectedRows = updatePs.executeUpdate();
                return affectedRows > 0;
            } else {
                return false; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteRejectedContact(String receiverPhone, String senderPhone) throws RemoteException {
        String getIdQuery = "SELECT id FROM UserContact " +
                "WHERE receiverID = (SELECT userID FROM User WHERE phone = ?) " +
                "AND senderID = (SELECT userID FROM User WHERE phone = ?) " +
                "AND requestStatus = 'REJECT'";
        String deleteQuery = "DELETE FROM UserContact WHERE id = ?";

        try (Connection con = dm.getConnection();
                PreparedStatement getIdPs = con.prepareStatement(getIdQuery);
                PreparedStatement deletePs = con.prepareStatement(deleteQuery)) {

            getIdPs.setString(1, receiverPhone);
            getIdPs.setString(2, senderPhone);
            ResultSet rs = getIdPs.executeQuery();

            if (rs.next()) {
                int requestId = rs.getInt("id");
                deletePs.setInt(1, requestId);
                int affectedRows = deletePs.executeUpdate();
                return affectedRows > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private UserDTO convert(String  phone) {
        String query = "SELECT * FROM User WHERE phone = ?;";
        try (Connection con = dm.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement(query);) {
            
            
            preparedStatement.setString(1,phone);
            ResultSet rs = preparedStatement.executeQuery();

 


        UserDTO user = new UserDTO();
        if(rs == null)return user;
        try {
            if (!rs.next()) {
                return null;
            }
            // rs.next();
            user.setUserID(rs.getInt("userID"));
            user.setPhone(rs.getString("phone"));
            user.setName(rs.getString("name"));
            user.setCountry(rs.getString("country"));
            user.setGender(Gender.valueOf(rs.getString("gender")));
            user.setEmail(rs.getString("email"));
            user.setBirthdate(rs.getDate("birthdate"));
            user.setPassword(rs.getString("password"));
            user.setFirstLogin(rs.getBoolean("firstLogin"));
            user.setUserStatus(UserStatus.valueOf(rs.getString("userStatus")));
            try {
                user.setUserMode(UserMode.valueOf(rs.getString("userMode")));
            } catch (IllegalArgumentException | NullPointerException e) {
                user.setUserMode(null);
            }
            try {
                user.setBio(rs.getString("bio"));
            } catch (IllegalArgumentException | NullPointerException e) {
                user.setBio(null);
            }

            if (rs.getString("userPicture") != null && rs.getString("userPicture").length() > 0) {
                user.setUserPicture(images.downloadPP(rs.getString("userPicture")));
                System.out.println(user.getUserPicture().length);

            } else {
                // System.out.println("why");
                user.setUserPicture(null);
            }

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }        }
         catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
     /* ------------------My Functions Implementation---Block Feature--------------------- */

        /* 
        //my Implementation
        @Override
        public List<UserDTO> findAllContactsBLOCKED(String userPhone) throws RemoteException {
        List<UserDTO> blockedContacts = new ArrayList<>();
        
        String getUserIDQuery = "SELECT userID FROM User WHERE phone = ?";
        String getBlockedContactsQuery =
                "(SELECT u.* FROM UserContact uc JOIN User u ON uc.receiverID = u.userID " +
                "WHERE uc.senderID = ? AND uc.requestStatus = 'Block') " +
                "UNION " +
                "(SELECT u.* FROM UserContact uc JOIN User u ON uc.senderID = u.userID " +
                "WHERE uc.receiverID = ? AND uc.requestStatus = 'Block')";
        
        try (Connection con = dm.getConnection();
             PreparedStatement psGetUserID = con.prepareStatement(getUserIDQuery)) {
        
            psGetUserID.setString(1, userPhone);
            ResultSet rsUser = psGetUserID.executeQuery();
        
            if (!rsUser.next()) {
                return blockedContacts; 
            }
        
            int userID = rsUser.getInt("userID");
        
            try (PreparedStatement psContacts = con.prepareStatement(getBlockedContactsQuery)) {
                psContacts.setInt(1, userID);
                psContacts.setInt(2, userID);
                ResultSet rs = psContacts.executeQuery();
        
                while (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setUserID(rs.getInt("userID"));
                    user.setPhone(rs.getString("phone"));
                    user.setName(rs.getString("name"));
                    user.setCountry(rs.getString("country"));
                    user.setGender(Gender.valueOf(rs.getString("gender")));
                    user.setEmail(rs.getString("email"));
                    user.setBirthdate(rs.getDate("birthdate"));
                    user.setUserStatus(UserStatus.valueOf(rs.getString("userStatus")));
        
                    String mode = rs.getString("userMode");
                    user.setUserMode(mode != null ? UserMode.valueOf(mode) : UserMode.AVAILABLE);
        
                    String bio = rs.getString("bio");
                    user.setBio(bio != null ? bio : "Hi, I'm using towk!");
        
                    if (rs.getString("userPicture") != null && rs.getString("userPicture").length() > 0) {
                        user.setUserPicture(images.downloadPP(rs.getString("userPicture")));
                    } else {
                        user.setUserPicture(null);
                    }
        
                    blockedContacts.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return blockedContacts;
        }
        
        */

public boolean isContactBlocked(String userPhone, String contactPhone) throws RemoteException {
    String checkBlockQuery = 
        "SELECT uc.requestStatus " +
        "FROM UserContact uc " +
        "JOIN User sender ON uc.senderID = sender.userID " +
        "JOIN User receiver ON uc.receiverID = receiver.userID " +
        "WHERE ((sender.phone = ? AND receiver.phone = ?) " +
        "OR (sender.phone = ? AND receiver.phone = ?)) " +
        "AND uc.requestStatus = 'BLOCK'";
        
    try (Connection con = dm.getConnection();
         PreparedStatement ps = con.prepareStatement(checkBlockQuery)) {
            
        ps.setString(1, userPhone);
        ps.setString(2, contactPhone);
        ps.setString(3, contactPhone);
        ps.setString(4, userPhone);
        
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next(); // Returns true if a blocked record exists
        }
    } catch (SQLException e) {
        System.err.println("SQL Error checking block status: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

public boolean blockContact(String blockerPhone, String blockedPhone) throws RemoteException {
    if (isContactBlocked(blockerPhone, blockedPhone)) {
        System.out.println("Contact is already blocked");
        return false;
    }

    String getIdQuery =
    "SELECT uc.id, uc.senderID, uc.receiverID, uc.requestStatus " +
    "FROM UserContact uc " +
    "JOIN User sender ON uc.senderID = sender.userID " +
    "JOIN User receiver ON uc.receiverID = receiver.userID " +
    "WHERE ((sender.phone = ? AND receiver.phone = ?) " +
    "OR (sender.phone = ? AND receiver.phone = ?)) " +
    "AND uc.requestStatus = 'ACCEPT'";

    String updateQuery =
        "UPDATE UserContact SET requestStatus = 'BLOCK', blockedBy = (SELECT userID FROM User WHERE phone = ?) WHERE id = ?";

    try (Connection con = dm.getConnection();
         PreparedStatement getIdPs = con.prepareStatement(getIdQuery)) {
        
        getIdPs.setString(1, blockerPhone);
        getIdPs.setString(2, blockedPhone);
        getIdPs.setString(3, blockedPhone);
        getIdPs.setString(4, blockerPhone);
        
        try (ResultSet rs = getIdPs.executeQuery()) {
            if (rs.next()) {
                int requestId = rs.getInt("id");

                try (PreparedStatement updatePs = con.prepareStatement(updateQuery)) {
                    updatePs.setString(1, blockerPhone);  // Store blocker’s ID
                    updatePs.setInt(2, requestId);
                    int affectedRows = updatePs.executeUpdate();
                    return affectedRows > 0;
                }
            }
            return false;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

@Override
public boolean isUserBlocker(String blockerPhone, String blockedPhone) throws RemoteException {
    String query = """
        SELECT 1
        FROM UserContact uc
        JOIN User blocker ON uc.blockedBy = blocker.userID
        JOIN User sender ON uc.senderID = sender.userID
        JOIN User receiver ON uc.receiverID = receiver.userID
        WHERE blocker.phone = ?
          AND ((sender.phone = ? AND receiver.phone = ?) OR (sender.phone = ? AND receiver.phone = ?))
          AND uc.requestStatus = 'BLOCK'
    """;

    try (Connection con = dm.getConnection();
         PreparedStatement ps = con.prepareStatement(query)) {
         
        ps.setString(1, blockerPhone); 
        ps.setString(2, blockerPhone);
        ps.setString(3, blockedPhone);
        ps.setString(4, blockedPhone);
        ps.setString(5, blockerPhone);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();  // Returns true if blocker exists
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


public boolean unblockContact(String blockerPhone, String blockedPhone) throws RemoteException {
    String getIdQuery =
    "SELECT uc.id " +
    "FROM UserContact uc " +
    "JOIN User sender ON uc.senderID = sender.userID " +
    "JOIN User receiver ON uc.receiverID = receiver.userID " +
    "WHERE ((sender.phone = ? AND receiver.phone = ?) " +
    "OR (sender.phone = ? AND receiver.phone = ?)) " +
    "AND uc.requestStatus = 'BLOCK'";

    String updateQuery = "UPDATE UserContact SET requestStatus = 'ACCEPT', blockedBy = NULL WHERE id = ?";

    try (Connection con = dm.getConnection();
         PreparedStatement getIdPs = con.prepareStatement(getIdQuery)) {
           
        getIdPs.setString(1, blockerPhone);
        getIdPs.setString(2, blockedPhone);
        getIdPs.setString(3, blockedPhone);
        getIdPs.setString(4, blockerPhone);
       
        try (ResultSet rs = getIdPs.executeQuery()) {
            if (rs.next()) {
                int requestId = rs.getInt("id");
               
                try (PreparedStatement updatePs = con.prepareStatement(updateQuery)) {
                    updatePs.setInt(1, requestId);
                    int affectedRows = updatePs.executeUpdate();
                    System.out.println("Unblock affected " + affectedRows + " rows");
                    return affectedRows > 0;
                }
            } else {
                System.out.println("No blocked contact record found");
                return false;
            }
        }
    } catch (SQLException e) {
        System.err.println("SQL Error during unblock: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

 


private String readEmail(String name){
    String query = "SELECT * FROM User WHERE phone = ?;";

 try (Connection con = dm.getConnection();
         WebRowSet rs = RowSetProvider.newFactory().createWebRowSet();) {
     rs.setCommand(query);
     rs.setString(1, name);
     rs.execute(con);

     if (rs.next()) {


         return rs.getString("email");
     }

 } catch (SQLException e) {
     e.printStackTrace();
 }
 return "";
}

private String readName(String name){
    String query = "SELECT * FROM User WHERE phone = ?;";

 try (Connection con = dm.getConnection();
         WebRowSet rs = RowSetProvider.newFactory().createWebRowSet();) {
     rs.setCommand(query);
     rs.setString(1, name);
     rs.execute(con);

     if (rs.next()) {


         return rs.getString("name");
     }

 } catch (SQLException e) {
     e.printStackTrace();
 }
 return "";
}



}
