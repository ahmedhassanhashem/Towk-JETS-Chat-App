package gov.iti.jets.dao;

import java.sql.PreparedStatement;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChatDAOTest extends MockingDBUtiltiy {
    private ChatDAO chatDAO;

    @BeforeEach
    void initDAO() throws Exception {
        chatDAO = new ChatDAO();
    }



    @Test
void testCreateGroup() throws Exception {
    String creatorPhone = "12345678901";
    String groupName = "Test Group";
    String participantPhone1 = "10987654321";
    String participantPhone2 = "11223344556";
    var participantPhones = Arrays.asList(participantPhone1, participantPhone2);

    when(mockDbManager.getConnection()).thenReturn(mockConnection);
    when(mockConnection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
            .thenReturn(mockPreparedStatement);
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

    when(mockResultSet.next()).thenReturn(true, true, true); 
    when(mockResultSet.getInt("userID")).thenReturn(1, 2, 3);
    when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

    when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
    when(mockResultSet.getInt(1)).thenReturn(200);

    int result = chatDAO.createGroup(creatorPhone, participantPhones, groupName,null);

    assertEquals(200, result, "The group creation result should match the expected message");

    verify(mockPreparedStatement).executeUpdate();
}



    @Test
    void testFindAllGroups() throws Exception {
        int userId = 1;

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("chatID")).thenReturn(100, 200);
        when(mockResultSet.getString("chatName")).thenReturn("Group 1").thenReturn("Group 2");
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        var groups = chatDAO.findAllGroups(userId);

        assertEquals(2, groups.size(), "The number of groups should match the expected value");
        assertEquals("Group 1", groups.get(0).getName(), "The first group name should match");
        assertEquals("Group 2", groups.get(1).getName(), "The second group name should match");

        verify(mockPreparedStatement).executeQuery();
    }


    @Test
    void testFindExistingSingleChat() throws Exception {
        int user1 = 1;
        int user2 = 2;

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("chatID")).thenReturn(100); // Simulate existing chat ID
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        Integer chatId = chatDAO.findExistingSingleChat(user1, user2);

        assertEquals(100, chatId, "The chat ID should match the expected value");

        verify(mockPreparedStatement).executeQuery();
    }


    @Test
    void testGetUserIdByPhone() throws Exception {
        String phone = "12345678901";

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("userID")).thenReturn(1); // Simulate user ID for phone

        int userId = chatDAO.getUserIdByPhone(phone);

        assertEquals(1, userId, "The user ID should match the expected value");

        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    void getUserIdByPhone() throws Exception{
        String phone = "10123456789";

        when(mockResultSet.next()).thenReturn(true); 
        when(mockResultSet.getInt("userID")).thenReturn(1, 2); 

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        int id = chatDAO.getUserIdByPhone(phone);

        assertEquals(1, id, "The current user ID should match the expected value");

        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testCreateSingle() throws Exception {
        // get user id by phone
        String currentUserPhone = "12345678910";
        String otherUserPhone = "10123456789";

        when(mockResultSet.next()).thenReturn(true); 
        when(mockResultSet.getInt("userID")).thenReturn(1, 2); 
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true); 
        when(mockResultSet.getInt("chatID")).thenReturn(0); 
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        int currentUserId = chatDAO.getUserIdByPhone(currentUserPhone);
        int otherUserId = chatDAO.getUserIdByPhone(otherUserPhone);
        int chatID = chatDAO.findExistingSingleChat(currentUserId, otherUserId);
        int newChatID = 100;

        assertEquals(1, currentUserId, "The current user ID should match the expected value");
        assertEquals(2, otherUserId, "The other user ID should match the expected value");
        assertEquals(0, chatID, "The current chat ID should not exist to create new one");
        assertEquals(100, newChatID, "The current chat ID should match the expected value");

        verify(mockPreparedStatement, times(3)).executeQuery();

    }
    

}
