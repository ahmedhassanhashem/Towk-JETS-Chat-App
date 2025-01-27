package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import gov.iti.jets.dto.ContactDTO;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContactDAO{

    DatabaseConnectionManager dm;

    public ContactDAO() {
        dm = DatabaseConnectionManager.getInstance();
    }

    

public String create(String senderPhone, String receiverPhone) {
    if (senderPhone.length() != 11 || receiverPhone.length() != 11) {
        return "Invalid phone number format";
    }

    String query = "INSERT INTO UserContact (senderID, receiverID, requestStatus) " + 
                   "VALUES (" +
                   "    (SELECT userID FROM User WHERE phone = ?), " +
                   "    (SELECT userID FROM User WHERE phone = ?), " +
                   "    'PENDING'" +
                   ")";

    try (Connection con = dm.getConnection();
        PreparedStatement ps = con.prepareStatement(query)) {

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

private boolean userExists(String phone) throws SQLException {
    String query = "SELECT 1 FROM User WHERE phone = ?";
    try (Connection con = dm.getConnection();
        PreparedStatement ps = con.prepareStatement(query)) {
        ps.setString(1, phone);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}
    


    // to get all the contacts to show in the contacts list
   public ObservableList<UserDTO> findAllContactsACCEPTED(String userPhone) {
    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    String getUserIDQuery = "SELECT userID FROM User WHERE phone = ?";
    String getContactsQuery = 
        "(SELECT u.* FROM UserContact uc JOIN User u ON uc.receiverID = u.userID " +
        "WHERE uc.senderID = ? AND uc.requestStatus = 'ACCEPT') " +
        "UNION " +
        "(SELECT u.* FROM UserContact uc JOIN User u ON uc.senderID = u.userID " +
        "WHERE uc.receiverID = ? AND uc.requestStatus = 'ACCEPT')";

    try (Connection con = dm.getConnection();
        PreparedStatement psGetUserID = con.prepareStatement(getUserIDQuery)) {

        psGetUserID.setString(1, userPhone);
        ResultSet rsUser = psGetUserID.executeQuery();
        if (!rsUser.next()) return contacts; // User not found

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
                if(mode == null)
                    user.setUserMode(UserMode.AVAILABLE);
                else
                    user.setUserMode(UserMode.valueOf(re.getString("userMode")));

                String bio = re.getString("bio");
                if(bio != null)
                    user.setBio(bio);
                else
                    user.setBio("Hi im using towk!");

                    // need modification 
                byte[] pic = re.getBytes("userPicture");
                if(bio == null)
                        /// default pic will set here 
                    user.setUserPicture(pic);
                else
                    user.setUserPicture(pic);

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
    public ObservableList<UserDTO> findAllContactsREJECTED(String userPhone) {
        ObservableList<UserDTO> rejectedContacts = FXCollections.observableArrayList();
        
        String query =" SELECT u.userID, u.phone, u.name, u.country, u.userStatus " +
                        "        FROM UserContact uc" + 
                        "        JOIN User u ON uc.receiverID = u.userID " + 
                        "        WHERE uc.senderID = ? " + //
                        "        AND uc.requestStatus = 'REJECT'";
    
        try (Connection con = dm.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT userID FROM User WHERE phone = ?")) {
            
            ps.setString(1, userPhone);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return rejectedContacts;
            
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
    public ObservableList<UserDTO> findAllContactsPENDING(String userPhone) {
        ObservableList<UserDTO> pendingReceived = FXCollections.observableArrayList();
    
    String query = "SELECT u.* "+
                "        FROM UserContact uc"+
                "        JOIN User u ON uc.senderID = u.userID " +
                "        WHERE uc.receiverID = ? "+
                "        AND uc.requestStatus = 'PENDING'"+
                "        ORDER BY u.name ASC";

    try (Connection con = dm.getConnection();
        PreparedStatement psGetUser = con.prepareStatement("SELECT userID FROM User WHERE phone = ?")) {
        
        // Get current user's ID (the receiver)
        psGetUser.setString(1, userPhone);
        ResultSet rsUser = psGetUser.executeQuery();
        if (!rsUser.next()) return pendingReceived;
        
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





class Test {
    public static void main(String[] args) {
        ContactDAO contactDAO = new ContactDAO();
        
        // Test phone numbers from your initial INSERT statements
        String johnPhone = "01012345678";
        String janePhone = "01098765432";
        String invalidPhone = "00000000000";

        System.out.println("=== Running ContactDAO Tests ===");

        // Test 1: Create Contact Request
        testCreateRequest(contactDAO, johnPhone, janePhone);      // Valid request
        testCreateRequest(contactDAO, johnPhone, invalidPhone);   // Invalid receiver
        testCreateRequest(contactDAO, invalidPhone, janePhone);   // Invalid sender

        // Test 2: Accepted Contacts
        testAcceptedContacts(contactDAO, johnPhone);
        testAcceptedContacts(contactDAO, janePhone);

        // Test 3: Rejected Contacts
        testRejectedContacts(contactDAO, johnPhone);

        // Test 4: Pending Requests
        testPendingRequests(contactDAO, janePhone);
    }

    private static void testCreateRequest(ContactDAO dao, String sender, String receiver) {
        System.out.println("\n[Test] Sending request from " + sender + " to " + receiver);
        String result = dao.create(sender, receiver);
        System.out.println("Result: " + (result != null ? "Success" : "Failed"));
    }

    private static void testAcceptedContacts(ContactDAO dao, String userPhone) {
        System.out.println("\n[Test] Accepted Contacts for " + userPhone);
        try {
            ObservableList<UserDTO> contacts = dao.findAllContactsACCEPTED(userPhone);
            System.out.println("Found " + contacts.size() + " accepted contacts:");
            contacts.forEach(c -> System.out.println(" - " + c.getName() + " (" + c.getPhone() + ")"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void testRejectedContacts(ContactDAO dao, String userPhone) {
        System.out.println("\n[Test] Rejected Contacts for " + userPhone);
        try {
            ObservableList<UserDTO> contacts = dao.findAllContactsREJECTED(userPhone);
            System.out.println("Found " + contacts.size() + " rejected contacts:");
            contacts.forEach(c -> System.out.println(" - " + c.getName() + " (" + c.getPhone() + ")"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void testPendingRequests(ContactDAO dao, String userPhone) {
        System.out.println("\n[Test] Pending Requests for " + userPhone);
        try {
            ObservableList<UserDTO> contacts = dao.findAllContactsPENDING(userPhone);
            System.out.println("Found " + contacts.size() + " pending requests:");
            contacts.forEach(c -> System.out.println(" - " + c.getName() + " (" + c.getPhone() + ")"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}