package gov.iti.jets.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.List;

import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.WebRowSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import gov.iti.jets.dto.MessageDTO;

public class MessageDAOTest extends MockingDBUtiltiy {

    private MessageDAO messageDAO;
    private static MockedStatic<RowSetProvider> rowSetProviderMock;
    RowSetFactory mockRowSetFactory;
    WebRowSet mockRowSet;


    @BeforeAll
    static void init(){
        MockingDBUtiltiy.init();
        rowSetProviderMock = Mockito.mockStatic(RowSetProvider.class);
    }

    @BeforeEach
    void initDAO() throws Exception {
        mockRowSetFactory = Mockito.mock(RowSetFactory.class);
        mockRowSet = Mockito.mock(WebRowSet.class);
        messageDAO = new MessageDAO();
    }


    @AfterAll
    static void close(){
        rowSetProviderMock.close();
        // MockingDBUtiltiy.tearDown();
    }


    @Test
    void testCreateMessageWithoutAttachment() throws Exception {
        String content = "Test message without attachment";
        int chatID = 1;
        int userID = 1;
        Date msgDate = new Date(System.currentTimeMillis());
        MessageDTO message = new MessageDTO(content, chatID, userID, msgDate);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        MessageDTO createdMessage = messageDAO.create(message);

        assertNotNull(createdMessage, "The created message should not be null");
        assertEquals(content, createdMessage.getMessageContent(), "Message content should match");
        assertEquals(chatID, createdMessage.getChatID(), "Chat ID should match");
        assertEquals(userID, createdMessage.getUserID(), "User ID should match");
        assertEquals(msgDate, createdMessage.getMessageDate(), "Message date should match");
        assertEquals(0, createdMessage.getAttachmentID(), "Attachment ID should be 0 when not provided");
    }

    @Test
    void testCreateMessageWithAttachment() throws Exception {
        String content = "Test message with attachment";
        int chatID = 1;
        int userID = 2;
        Date msgDate = new Date(System.currentTimeMillis());
        int attachmentID = 10;
        MessageDTO message = new MessageDTO(content, chatID, userID, msgDate, attachmentID);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        MessageDTO createdMessage = messageDAO.create(message);

        assertNotNull(createdMessage, "The created message should not be null");
        assertEquals(attachmentID, createdMessage.getAttachmentID(), "Attachment ID should match");
    }

    @Test
    void testCreateBlankMessageWithAttachment() throws Exception {
        String blankContent = "";
        int chatID = 1;
        int userID = 1;
        Date msgDate = new Date(System.currentTimeMillis());
        int nonZeroAttachmentID = 5;
        MessageDTO message = new MessageDTO(blankContent, chatID, userID, msgDate, nonZeroAttachmentID);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        MessageDTO createdMessage = messageDAO.create(message);

        assertNotNull(createdMessage, "A blank message with a non-zero attachment should be created.");
        assertEquals(blankContent, createdMessage.getMessageContent(), "Message content should be blank.");
        assertEquals(nonZeroAttachmentID, createdMessage.getAttachmentID(), "Attachment ID should match the provided non-zero value.");
    }

    @Test
    void testCreateBlankMessageWithBlankAttachment() throws Exception {
        String blankContent = "";
        int chatID = 1;
        int userID = 1;
        Date msgDate = new Date(System.currentTimeMillis());
        int blankAttachmentID = 0;
        MessageDTO message = new MessageDTO(blankContent, chatID, userID, msgDate, blankAttachmentID);

        MessageDTO createdMessage = messageDAO.create(message);

        assertNull(createdMessage, "A blank message with a blank attachment should not be created (should return null).");
    }

    @Test
    void testFindAllMessages() throws Exception {
        int chatID = 100;
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());
        
        
        when(mockRowSet.next()).thenReturn(true, true, true, true, false);
        when(mockRowSet.getString("messageContent"))
                .thenReturn("first message", "second message", "third message", "fourth message");

        when(mockRowSet.getInt("chatID"))
                .thenReturn(chatID, chatID, chatID, chatID);

        when(mockRowSet.getInt("userID"))
                .thenReturn(10, 11, 10, 11);

        when(mockRowSet.getDate("messageDate"))
                .thenReturn(now, now, now, now);

        when(mockRowSet.getInt("attachmentID"))
                .thenReturn(0, 0, 0, 0);
        
     
        when(mockRowSetFactory.createWebRowSet()).thenReturn(mockRowSet);
        rowSetProviderMock.when(RowSetProvider::newFactory).thenReturn(mockRowSetFactory);
        
        List<MessageDTO> messages = messageDAO.findAllMessages(chatID);
        
        assertNotNull(messages, "Message list should not be null");
        assertEquals(4, messages.size(), "There should be exactly four messages for the chat");
        
        assertEquals("first message", messages.get(0).getMessageContent(), "First message content should match");
        assertEquals("second message", messages.get(1).getMessageContent(), "Second message content should match");
        assertEquals("third message", messages.get(2).getMessageContent(), "Third message content should match");
        assertEquals("fourth message", messages.get(3).getMessageContent(), "Fourth message content should match");
    
    }


    @Test
    void testFindLastMessageGroup() throws Exception {
        int chatID = 200;
        Date now = new Date(System.currentTimeMillis());

        when(mockRowSet.next()).thenReturn(true, true, false);
        when(mockRowSet.getString("messageContent")).thenReturn("First group message", "Last group message");
        when(mockRowSet.getInt("chatID")).thenReturn(chatID, chatID);
        when(mockRowSet.getInt("userID")).thenReturn(10, 11);
        when(mockRowSet.getDate("messageDate")).thenReturn(now, now);
        when(mockRowSet.getInt("attachmentID")).thenReturn(0, 0);

        when(mockRowSetFactory.createWebRowSet()).thenReturn(mockRowSet);
        rowSetProviderMock.when(RowSetProvider::newFactory).thenReturn(mockRowSetFactory);

        String lastMessage = messageDAO.findLastMessageGroup(chatID);
        assertEquals("Last group message", lastMessage);
        
    }

    @Test
    void testFindLastMessageForChat() throws Exception {
        int chatID = 300;
        Date now = new Date(System.currentTimeMillis());
        when(mockRowSet.next()).thenReturn(true, true, false);
        when(mockRowSet.getString("messageContent")).thenReturn("First chat message", "Last chat message");
        when(mockRowSet.getInt("chatID")).thenReturn(chatID, chatID);
        when(mockRowSet.getInt("userID")).thenReturn(20, 21);
        when(mockRowSet.getDate("messageDate")).thenReturn(now, now);
        when(mockRowSet.getInt("attachmentID")).thenReturn(0, 0);

        when(mockRowSetFactory.createWebRowSet()).thenReturn(mockRowSet);
        rowSetProviderMock.when(RowSetProvider::newFactory).thenReturn(mockRowSetFactory);

        MessageDTO lastMessage = messageDAO.findLastMessage(chatID);
        assertNotNull(lastMessage);
        assertEquals("Last chat message", lastMessage.getMessageContent());
    }

    @Test
    void testFindLastMessageForUsers() throws Exception {

        int user1 = 1;
        int user2 = 2;
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("chatID")).thenReturn(100); // Simulate existing chat ID
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        Integer chatId = new ChatDAO().findExistingSingleChat(user1, user2);

        assertEquals(100, chatId, "The chat ID should match the expected value");

        verify(mockPreparedStatement).executeQuery();

        
        
        
        when(mockRowSet.next()).thenReturn(true, true, true, true, false);
        when(mockRowSet.getString("messageContent"))
                .thenReturn("first message", "second message", "third message", "fourth message");

        when(mockRowSet.getInt("chatID"))
                .thenReturn(chatId, chatId, chatId, chatId);

        when(mockRowSet.getInt("userID"))
                .thenReturn(1, 2, 1, 2);

        when(mockRowSet.getDate("messageDate"))
                .thenReturn(now, now, now, now);

        when(mockRowSet.getInt("attachmentID"))
                .thenReturn(0, 0, 0, 0);
        
     
        when(mockRowSetFactory.createWebRowSet()).thenReturn(mockRowSet);
        rowSetProviderMock.when(RowSetProvider::newFactory).thenReturn(mockRowSetFactory);
        
        List<MessageDTO> messages = messageDAO.findAllMessages(chatId);
        
        assertNotNull(messages, "Message list should not be null");
        assertEquals(4, messages.size(), "There should be exactly four messages for the chat");


        when(mockRowSet.next()).thenReturn(true, true, false);
        when(mockRowSet.getString("messageContent")).thenReturn("First chat message", "Last chat message");
        when(mockRowSet.getInt("chatID")).thenReturn(chatId, chatId);
        when(mockRowSet.getInt("userID")).thenReturn(user1, user2);
        when(mockRowSet.getDate("messageDate")).thenReturn(now, now);
        when(mockRowSet.getInt("attachmentID")).thenReturn(0, 0);

        
        when(mockRowSetFactory.createWebRowSet()).thenReturn(mockRowSet);
        rowSetProviderMock.when(RowSetProvider::newFactory).thenReturn(mockRowSetFactory);

        
        String lastMessage = messageDAO.findLastMessage(user1, user2);

        
        assertNotNull(lastMessage, "The last message should not be null");
        assertEquals("Last ch...", lastMessage, "The last message content should match");
}

    
        

}

