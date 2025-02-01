package gov.iti.jets.dao;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;

import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;

import java.util.List;


public class UserChatDAOTest extends MockingDBUtiltiy{

    private UserChatDAO userChatDAO;

    @BeforeEach
    void initDAO() throws Exception {
        userChatDAO = new UserChatDAO();
    }



    @Test
    void testFindAll() throws Exception {

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next())
            .thenReturn(true)  
            .thenReturn(true)  
            .thenReturn(false); 
    
        when(mockResultSet.getInt("userID"))
            .thenReturn(1)    
            .thenReturn(2);   
    
        when(mockResultSet.getString("phone"))
            .thenReturn("1234567890")  
            .thenReturn("0987654321"); 
    
        when(mockResultSet.getString("name"))
            .thenReturn("Doaa")  
            .thenReturn("Lilly"); 
    
        when(mockResultSet.getString("gender"))
            .thenReturn("MALE")   
            .thenReturn("FEMALE"); 
    
        when(mockResultSet.getString("userStatus"))
            .thenReturn("ONLINE")  
            .thenReturn("OFFLINE");
    

            // cuz dao union the user MODE
        when(mockResultSet.getString("userMode"))
            .thenReturn("AVAILABLE", "BUSY");         
    
        List<UserDTO> result = userChatDAO.findAll(1);
    
        assertEquals(UserMode.AVAILABLE, result.get(0).getUserMode());
    
        assertEquals(UserMode.BUSY, result.get(1).getUserMode());
    }
    


    @Test
    void testGetChatParticipants() throws Exception{
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getInt("userID")).thenReturn(11, 22, 33);

        List<Integer> participants = userChatDAO.getChatParticipants(5);

        assertEquals(List.of(11, 22, 33), participants);
        verify(mockPreparedStatement).setInt(eq(1), eq(5));

    }


    @Test
    void testGetUserChats() throws Exception{
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("chatID")).thenReturn(101, 202);

        List<Integer> chats = userChatDAO.getUserChats(10);

        assertEquals(List.of(101, 202), chats);
        verify(mockPreparedStatement).setInt(eq(1), eq(10));
    }
  


    // private void mockUserResultSet(int userId, String phone, String name) throws SQLException {
    //     // Common fields
    //     when(mockResultSet.getInt("userID")).thenReturn(userId);
    //     when(mockResultSet.getString("phone")).thenReturn(phone);
    //     when(mockResultSet.getString("name")).thenReturn(name);
    //     when(mockResultSet.getString("country")).thenReturn("Country");
    //     when(mockResultSet.getString("gender")).thenReturn("MALE");
    //     when(mockResultSet.getString("email")).thenReturn("test@example.com");
    //     // when(mockResultSet.getDate("birthdate")).thenReturn(new java.sql.Date(System.currentTimeMillis()));
    //     when(mockResultSet.getString("password")).thenReturn("password");
    //     when(mockResultSet.getBoolean("firstLogin")).thenReturn(false);
    //     when(mockResultSet.getString("userStatus")).thenReturn("ONLINE");
    //     when(mockResultSet.getString("userMode")).thenReturn("AVAILABLE");
    //     // when(mockResultSet.getString("userPicture")).thenReturn("path/to/image.jpg");
    // }

}