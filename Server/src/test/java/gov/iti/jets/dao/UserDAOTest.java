package gov.iti.jets.dao;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.rmi.RemoteException;
import java.sql.*;


public class UserDAOTest {

    @Mock
    private DatabaseConnectionManager mockDbManager;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @InjectMocks
    private UserDAO userDAO;

    @BeforeEach
    void setup() throws Exception{
        MockitoAnnotations.openMocks(this);
        when(mockDbManager.getConnection()).thenReturn(mockConnection);
        userDAO = new UserDAO();
    }

    //valid case
    @Test
    void testCreateUserValid(){

    }

    //invalid
    @Test
    void testCreateInalid(){
        
    }

}
