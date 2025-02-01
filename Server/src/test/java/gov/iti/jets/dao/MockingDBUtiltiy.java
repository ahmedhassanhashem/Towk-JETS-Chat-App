package gov.iti.jets.dao;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class MockingDBUtiltiy {
    protected static MockedStatic<DatabaseConnectionManager> mockedDbManager;
    protected static DatabaseConnectionManager mockDbManager;
    protected Connection mockConnection;
    protected PreparedStatement mockPreparedStatement;
    protected ResultSet mockResultSet;

    @BeforeAll
    static void init() {
        mockedDbManager = Mockito.mockStatic(DatabaseConnectionManager.class);
        mockDbManager = Mockito.mock(DatabaseConnectionManager.class);
        mockedDbManager.when(DatabaseConnectionManager::getInstance).thenReturn(mockDbManager);
    }

    @BeforeEach
    protected void setup() throws Exception {
        mockConnection = Mockito.mock(Connection.class);
        mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);

        when(mockDbManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    }

    @AfterAll
    protected static void tearDown() {
        mockedDbManager.close();
    }
}
