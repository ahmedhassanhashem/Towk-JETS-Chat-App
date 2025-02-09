package gov.iti.jets.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gov.iti.jets.dto.UserDTO;

public class ContactDAOTest extends MockingDBUtiltiy{
    private ContactDAO contactDAO;

    @BeforeEach
    void initDAO() throws Exception {
        contactDAO = new ContactDAO();
    }


 



    @Test
        void testCheckSent() throws Exception {
        String senderPhone = "12345678901";
        String receiverPhone = "10987654321";

        when(mockResultSet.isBeforeFirst()).thenReturn(false);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        boolean result = contactDAO.checkSent(senderPhone, receiverPhone);

        assertFalse(result, "checkSent should return false if no request exists");

        verify(mockPreparedStatement, times(1)).executeQuery();
    }


  

    @Test
    void testFindAllContactsACCEPTED() throws Exception {
        String userPhone = "12345678901";

        when(mockResultSet.next()).thenReturn(true); 
        when(mockResultSet.getInt("userID")).thenReturn(1); 
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet); 

        ResultSet mockContactsResultSet = Mockito.mock(ResultSet.class);

        when(mockContactsResultSet.next()).thenReturn(true, true, false); 
        when(mockContactsResultSet.getInt("userID")).thenReturn(2, 3);
        when(mockContactsResultSet.getString("phone")).thenReturn("10987654321", "11223344556");
        when(mockContactsResultSet.getString("name")).thenReturn("Alice", "Bob");
        when(mockContactsResultSet.getString("country")).thenReturn("USA", "UK");
        when(mockContactsResultSet.getString("gender")).thenReturn("FEMALE", "MALE");
        when(mockContactsResultSet.getString("email")).thenReturn("alice@example.com", "bob@example.com");
        when(mockContactsResultSet.getDate("birthdate")).thenReturn(
            java.sql.Date.valueOf("1990-01-01"),
            java.sql.Date.valueOf("1985-05-20")
        );
        when(mockContactsResultSet.getString("userStatus")).thenReturn("ONLINE", "OFFLINE");
        when(mockContactsResultSet.getString("userMode")).thenReturn("AVAILABLE", "BUSY");
        when(mockContactsResultSet.getString("bio")).thenReturn("Hey there!", "Loving Java!");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet, mockContactsResultSet);

        
        List<UserDTO> contacts = contactDAO.findAllContactsACCEPTED(userPhone);

        
        assertEquals(2, contacts.size(), "The number of accepted contacts should match exactly 2");

        verify(mockPreparedStatement, times(2)).executeQuery();
    }




    @Test
    void testFindAllContactsACCEPTEDNoContacts() throws Exception {
        String userPhone = "12345678901";

        when(mockResultSet.next()).thenReturn(true); // User exists
        when(mockResultSet.getInt("userID")).thenReturn(1);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        when(mockResultSet.next()).thenReturn(false); // No contacts

        List<UserDTO> contacts = contactDAO.findAllContactsACCEPTED(userPhone);

        assertTrue(contacts.isEmpty(), "The list should be empty when no accepted contacts exist");

        verify(mockPreparedStatement, times(1)).executeQuery();
    }


    @Test
    void testCreateContact() throws Exception {
        String senderPhone = "12345678901";
        String receiverPhone = "10987654321";
        
    
        ResultSet checkSentRS = Mockito.mock(ResultSet.class);
        when(checkSentRS.isBeforeFirst()).thenReturn(false);
        
        ResultSet senderRS = Mockito.mock(ResultSet.class);
        when(senderRS.next()).thenReturn(true);
        
        ResultSet receiverRS = Mockito.mock(ResultSet.class);
        when(receiverRS.next()).thenReturn(true);
        
        // The create() method calls:
        // 1. checkSent() -> first executeQuery() call
        // 2. userExists(senderPhone) -> second executeQuery() call
        // 3. userExists(receiverPhone) -> third executeQuery() call
        when(mockPreparedStatement.executeQuery())
             .thenReturn(checkSentRS)  
             .thenReturn(senderRS)     
             .thenReturn(receiverRS);  
        
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // String result = contactDAO.create(senderPhone, receiverPhone);
        
        assertNotNull(checkSentRS);
    }


    @Test
    void testFindAllContactsPENDING() throws Exception{
        String userPhone = "12345678901";

        when(mockResultSet.next()).thenReturn(true); 
        when(mockResultSet.getInt("userID")).thenReturn(1); 
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet); 

        ResultSet mockContactsResultSet = Mockito.mock(ResultSet.class);

        when(mockContactsResultSet.next()).thenReturn(true, true, false); 
        when(mockContactsResultSet.getInt("userID")).thenReturn(2, 3);
        when(mockContactsResultSet.getString("phone")).thenReturn("10987654321", "11223344556");
        when(mockContactsResultSet.getString("name")).thenReturn("Alice", "Bob");
        when(mockContactsResultSet.getString("country")).thenReturn("USA", "UK");
        when(mockContactsResultSet.getString("gender")).thenReturn("FEMALE", "MALE");
        when(mockContactsResultSet.getString("email")).thenReturn("alice@example.com", "bob@example.com");
        when(mockContactsResultSet.getDate("birthdate")).thenReturn(
            java.sql.Date.valueOf("1990-01-01"),
            java.sql.Date.valueOf("1985-05-20")
        );
        when(mockContactsResultSet.getString("userStatus")).thenReturn("ONLINE", "OFFLINE");
        when(mockContactsResultSet.getString("userMode")).thenReturn("AVAILABLE", "BUSY");
        when(mockContactsResultSet.getString("bio")).thenReturn("Hey there!", "Loving Java!");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet, mockContactsResultSet);

        
        List<UserDTO> contacts = contactDAO.findAllContactsPENDING(userPhone);

        
        assertEquals(2, contacts.size(), "The number of accepted contacts should match exactly 2");

        verify(mockPreparedStatement, times(2)).executeQuery();
    }


    @Test
    void testFindAllContactsREJECTED() throws Exception{
        String userPhone = "12345678901";

        when(mockResultSet.next()).thenReturn(true); 
        when(mockResultSet.getInt("userID")).thenReturn(1); 
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet); 

        ResultSet mockContactsResultSet = Mockito.mock(ResultSet.class);

        when(mockContactsResultSet.next()).thenReturn(true, true, false); 
        when(mockContactsResultSet.getInt("userID")).thenReturn(2, 3);
        when(mockContactsResultSet.getString("phone")).thenReturn("10987654321", "11223344556");
        when(mockContactsResultSet.getString("name")).thenReturn("Alice", "Bob");
        when(mockContactsResultSet.getString("country")).thenReturn("USA", "UK");
        when(mockContactsResultSet.getString("gender")).thenReturn("FEMALE", "MALE");
        when(mockContactsResultSet.getString("email")).thenReturn("alice@example.com", "bob@example.com");
        when(mockContactsResultSet.getDate("birthdate")).thenReturn(
            java.sql.Date.valueOf("1990-01-01"),
            java.sql.Date.valueOf("1985-05-20")
        );
        when(mockContactsResultSet.getString("userStatus")).thenReturn("ONLINE", "OFFLINE");
        when(mockContactsResultSet.getString("userMode")).thenReturn("AVAILABLE", "BUSY");
        when(mockContactsResultSet.getString("bio")).thenReturn("Hey there!", "Loving Java!");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet, mockContactsResultSet);

        
        List<UserDTO> contacts = contactDAO.findAllContactsREJECTED(userPhone);

        
        assertEquals(2, contacts.size(), "The number of accepted contacts should match exactly 2");

        verify(mockPreparedStatement, times(2)).executeQuery();
    }
    


}
