package gov.iti.jets.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.sql.*;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;


public class UserDAOTest {

    private static MockedStatic<DatabaseConnectionManager> mockedDbManager;
    private static DatabaseConnectionManager mockDbManager;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private UserDAO userDAO;

    @BeforeAll
    static void init() {
        mockedDbManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        mockDbManager = Mockito.mock(DatabaseConnectionManager.class);
        mockedDbManager.when(DatabaseConnectionManager::getInstance).thenReturn(mockDbManager);
    }

    @BeforeEach
    void setup() throws Exception {
        mockConnection = Mockito.mock(Connection.class);
        mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);

        when(mockDbManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        userDAO = new UserDAO();
    }

    @AfterAll
    static void tearDown() {
        mockedDbManager.close();
    }

    //valid case
    @Test
    void testCreateUserValid() throws Exception{
        UserDTO user = new UserDTO();
        user.setPhone("01234567890");
        user.setName("John Doe");
        user.setCountry("USA");
        user.setGender(Gender.MALE);
        user.setEmail("john@example.com");
        user.setBirthdate(Date.valueOf("1990-01-01"));
        user.setPassword("password123");

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        UserDTO createdUser = userDAO.create(user);

        assertNotNull(createdUser);
        assertEquals(user.getPhone(), createdUser.getPhone());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    //invalid
    @Test
    void testCreateUserInalid() throws Exception{
        UserDTO user = new UserDTO();
        user.setPhone("12367890"); // Invalid phone number
        UserDTO result = userDAO.create(user);
        assertNull(result);
    }

    //login
    @Test
    void testReadUser() throws Exception{
        UserDTO user = new UserDTO();
        user.setPhone("12345678910");
        user.setPassword("password123");

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("userID")).thenReturn(1);
        when(mockResultSet.getString("phone")).thenReturn("12345678910");
        when(mockResultSet.getString("name")).thenReturn("John Doe");
        when(mockResultSet.getString("country")).thenReturn("Egypt");
        when(mockResultSet.getString("gender")).thenReturn("MALE");
        when(mockResultSet.getString("email")).thenReturn("john@example.com");
        when(mockResultSet.getDate("birthdate")).thenReturn(Date.valueOf("1990-01-01"));
        when(mockResultSet.getString("password")).thenReturn("password123");
        // when(mockResultSet.getString("userPicture")).thenReturn("/path/to/image.jpg");
        when(mockResultSet.getString("bio")).thenReturn("Software Developer");
        when(mockResultSet.getBoolean("firstLogin")).thenReturn(true);
        when(mockResultSet.getString("userStatus")).thenReturn("ONLINE"); 
        when(mockResultSet.getString("userMode")).thenReturn("AVAILABLE"); 


        UserDTO result = userDAO.read(user);

        assertNotNull(result);
        assertEquals("12345678910", result.getPhone());
        assertEquals("password123", result.getPassword());
        verify(mockPreparedStatement, times(1)).executeQuery();

    }

    //update 
    @Test
    void testUpdateUser() throws Exception{
         when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        int result = userDAO.update(1, "New Name", "New Bio", UserMode.AVAILABLE);
        assertEquals(1, result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
    
    //update password
    @Test
    void testUpdatePasswordUser() throws Exception{
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        int result = userDAO.updatePassword(1, "newPassword");
        assertEquals(1, result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }


    //delete
    @Test
    void testDeleteUser() throws Exception{
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        userDAO.delete(1);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    // stats (gender , country, userStatus)
    @Test
void testGetUserStatistics() throws Exception {

    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    
    when(mockResultSet.next())
        .thenReturn(true)  
        .thenReturn(true) 
        .thenReturn(false);
    
    when(mockResultSet.getString("gender"))
        .thenReturn("MALE")
        .thenReturn("FEMALE"); 

    when(mockResultSet.getInt("count"))
        .thenReturn(10)
        .thenReturn(15);

    ObservableList<PieChart.Data> stats = userDAO.getUserStatistics("gender");

    assertEquals(2, stats.size());
    
    PieChart.Data first = stats.get(0);
    assertEquals("MALE", first.getName());
    assertEquals(10, first.getPieValue(), 0.001);
    
    PieChart.Data second = stats.get(1);
    assertEquals("FEMALE", second.getName());
    assertEquals(15, second.getPieValue(), 0.001);
    
    verify(mockPreparedStatement, times(1)).executeQuery();
}

}
