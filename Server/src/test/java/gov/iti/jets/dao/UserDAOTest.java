package gov.iti.jets.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;

import java.rmi.RemoteException;
import java.sql.*;


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
        
    }

    //login
    @Test
    void testReadUser(){

    }

    //update 
    @Test
    void testUpdateUser(){
        
    }
    
    //update password
    @Test
    void testUpdatePasswordUser(){
        
    }


    //delete
    @Test
    void testDeleteUser(){
        
    }

    // stats (gender , country, userStatus)
    @Test
    void testGetUserStatistics(){

    }

}
